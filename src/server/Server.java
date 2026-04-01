package server;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.ErrorDefs;
import common.MessageSerializer;
import common.MessageDefs;
import common.MessageHeader;
import common.models.messages.AnyErrorMessage;
import common.models.messages.TextMessage;


// Owner of host socket and all Client socket connections
public class Server {
	private ServerSocket socket;
	private List<Client> clients = Collections.synchronizedList(new ArrayList<Client>());
	private boolean closed = false;
	
	private Router<Controller> router;
	
	private Thread acceptThread;
	private Thread handleThread;
	
	public Server(int port, Controller controller) throws IOException, InvalidRouteException {
		socket = new ServerSocket(port);
		

		router = new Router<Controller>(controller);
		
		acceptThread = new Thread(() -> acceptAll());
		acceptThread.start();
		
		handleThread = new Thread(() -> handleAll());
		handleThread.start();
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
		
		try {
			if(handleThread != null)
				handleThread.join();
		} catch(InterruptedException ex) {}
	}
	
	public void RouteMessage(Client requester, MessageHeader header, byte[] content) throws NotFoundException, InvalidContentException {
		MessageContext context = new MessageContext(requester);
		
		try {
			router.route(context, header.getCode(), header.getSizes(), content);
		} catch (NotFoundException ex) {
			
			
			requester.sendMessage(new AnyErrorMessage(MessageDefs.INVALID_HEADER_ERROR, ErrorDefs.INVALID_OR_MISSING_ARG, header.getCode()).serialize());
			requester.close();
		} catch (InvalidContentException ex) {
			// TODO: ex.getIndex()
			requester.sendMessage(new AnyErrorMessage(MessageDefs.INVALID_HEADER_ERROR, ErrorDefs.INVALID_OR_MISSING_ARG, header.getCode()).serialize());
			requester.close();
		}
	}
	
	
	private void handleAll() {
		try {
			while(!closed) {
				List<Client> toRemove = new ArrayList<Client>();
				synchronized (clients) {
					for(Client client : clients) {
						
						if(client.HasError()) {
							System.out.println(client + " disconnected.");
							toRemove.add(client);
							continue;
						}
						
						client.handle(this);
					}
	
					for(Client client : toRemove) {
						client.close();
						clients.remove(client);
					}
				}
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
		}
		finally {
			for(Client client : clients) {
				client.close();
			}
			clients.clear();
		}
	}

	private void acceptAll() {
		while(!closed) {
			Socket accepted = null;
			try {
				accepted = socket.accept();
				accepted.setSoTimeout(100);
				Client client = new Client(accepted);

				synchronized (clients) {
					clients.add(client);
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
