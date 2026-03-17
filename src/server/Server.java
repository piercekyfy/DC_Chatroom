package server;

import java.io.*;
import java.net.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import server.client.Client;


// Owner of host socket and all Client socket connections
public class Server {
	private ServerSocket socket;
	private List<Client> clients = Collections.synchronizedList(new ArrayList<Client>());
	private boolean closed = false;
	
	private byte[] buffer = new byte[1024];
	
	private Controller controller;
	private Router router;
	
	private Thread acceptThread;
	private Thread handleThread;
	
	public Server(int port) throws IOException, InvalidRouteException {
		socket = new ServerSocket(port);
		
		controller = new Controller();
		router = new Router<Controller>(controller);
		
		acceptThread = new Thread(() -> acceptAll());
		acceptThread.start();
		
		handleThread = new Thread(() -> handleAll());
		handleThread.start();
	}
	
	public void Join() throws InterruptedException {
		acceptThread.join();
		handleThread.join();
	}
	
	public void RouteMessage(Client requester, int code, int[] sizes, byte[] content) throws NotFoundException, InvalidContentException {
		router.route(code, sizes, content);
	}
	
	
	private void handleAll() {
		while(!closed) {
			List<Client> toRemove = new ArrayList<Client>();
			synchronized (clients) {
				for(Client client : clients) {
					
					if(client.HasError()) {
						System.out.println(client + " disconnected unexpectedly.");
						toRemove.add(client);
						continue;
					}
					
					client.handle(this);
				}

				for(Client client : toRemove) {
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

	private void acceptAll() {
		while(!closed) {
			try {
				Socket accepted = socket.accept();
				Client client = new Client(accepted);
				System.out.println("Added client: " + client);
				synchronized (clients) {
					clients.add(client);
				}
			} catch(IOException ex) {
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
