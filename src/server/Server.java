package server;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import common.ErrorDefs;
import common.models.responses.UnrecoverableErrorResponse;
import common.models.messages.MessageHeader;
import common.models.responses.DisconnectedErrorResponse;

// Owner of host socket and all Client socket connections
public class Server {
	private static final int MAX_CLIENTS_PER_GROUP = 5;
	
	private SSLServerSocket socket;
	private Router<Controller> router;
	private Thread acceptThread;
	private boolean closed = false;
	
	private List<ClientGroup> clientGroups = Collections.synchronizedList(new ArrayList<>());
	
	public Server(SSLServerSocket socket, Controller controller) throws IOException, InvalidRouteException {
		this.socket = socket;
		router = new Router<Controller>(controller);
		
		acceptThread = new Thread(() -> acceptAll());
		acceptThread.start();
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public void close() {
		closed = true;
		
		try {
			if(socket != null)
				socket.close();
		} catch (IOException ex) {}
		
		try {
			if(acceptThread != null)
				acceptThread.join();
		} catch(InterruptedException ex) {}
		
		for(ClientGroup group : clientGroups) {
			group.stop();
		}
		clientGroups.clear();
	}
	
	public void routeMessage(Client requester, MessageHeader header, ByteBuffer content) {
		MessageContext context = new MessageContext(requester);
		
		try {
			router.route(context, header.getCode(), header.getSizes(), content);
		} catch (NotFoundException ex) {
			requester.setError();
		} catch (InvalidContentException ex) {
			requester.setError();
		}
	}
	
	private ClientGroup createNewClientGroup() {
		ClientGroup group = new ClientGroup();
		new Thread(() -> group.handle(this)).start();;
		return group;
	}

	private void acceptAll() {
		while(!closed) {
			SSLSocket accepted = null;
			try {
				accepted = (SSLSocket)socket.accept();
				accepted.setSoTimeout(500);
				Client client = new Client(accepted, this);
				
				synchronized(clientGroups) { 
					boolean added = false;
					for(ClientGroup group : clientGroups) {
						if(group.size() < MAX_CLIENTS_PER_GROUP) {
							group.add(client);
							added = true;
							break;
						}
					}
					
					if(!added) {
						ClientGroup newGroup = createNewClientGroup();
						newGroup.add(client);
						clientGroups.add(newGroup);
					}
				}
			} catch(IOException ex) {
				if(accepted != null)
					try {
						accepted.close();
					} catch (IOException e) {}
				continue;
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	}
}
