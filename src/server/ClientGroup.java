package server;

import java.util.ArrayList;
import java.util.List;

import common.ErrorDefs;
import common.models.responses.DisconnectedErrorResponse;
import common.models.responses.UnrecoverableErrorResponse;

public class ClientGroup {
	private List<Client> clients = new ArrayList<Client>();
	private boolean stopped = false;
	
	public void handle(Server server) {
		try {
			while(!stopped) {
				List<Client> toRemove = new ArrayList<Client>();
				synchronized (clients) {
					for(Client client : clients) {
						
						if(client.requestedDisconnect()) {
							try {
								client.sendImmediately(new DisconnectedErrorResponse(ErrorDefs.NONE, -1));
							} catch (Exception ex) {}
							toRemove.add(client);
							continue;
						}
						else if(client.hasError()) {			
							try {
								client.sendImmediately(new UnrecoverableErrorResponse(ErrorDefs.NONE, -1));
							} catch (Exception ex) {}
							
							toRemove.add(client);
							continue;
						}
						
						client.handle(server);
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
			synchronized(clients) {
				for(Client client : clients) {
					client.close();
				}
				clients.clear();
			}
		}
	}
	
	public void add(Client client) {
		synchronized(clients) {
			clients.add(client);
		}
	}
	
	public int size() {
		synchronized(clients) {
			return this.clients.size();
		}
	}
	
	public void stop() {
		this.stopped = true;
	}
	
	public boolean isStopped() {
		return this.stopped;
	}
}
