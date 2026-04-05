package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import common.ErrorDefs;
import common.MessageHeader;
import common.models.responses.UnrecoverableErrorResponse;
import common.models.responses.DisconnectedErrorResponse;

// Owner of host socket and all Client socket connections
public class Server {
	private SSLServerSocket socket;
	private List<Client> clients = Collections.synchronizedList(new ArrayList<Client>());
	private boolean closed = false;
	
	private Router<Controller> router;
	
	private Thread acceptThread;
	private Thread handleThread;
	
	public Server(SSLServerSocket socket, Controller controller) throws IOException, InvalidRouteException {
		this.socket = socket;
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
			requester.setError();
		} catch (InvalidContentException ex) {
			requester.setError();
		}
	}
	
	private void handleAll() {
		try {
			while(!closed) {
				List<Client> toRemove = new ArrayList<Client>();
				synchronized (clients) {
					for(Client client : clients) {
						
						if(client.requestedDisconnect()) {
							try {
								client.sendMessageImmediately(new DisconnectedErrorResponse(ErrorDefs.NONE, -1));
							} catch (Exception ex) {}
							toRemove.add(client);
							continue;
						}
						else if(client.HasError()) {			
							try {
								client.sendMessageImmediately(new UnrecoverableErrorResponse(ErrorDefs.NONE, -1));
							} catch (Exception ex) {}
							
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
			SSLSocket accepted = null;
			try {
				accepted = (SSLSocket)socket.accept();
				accepted.setSoTimeout(500);
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
