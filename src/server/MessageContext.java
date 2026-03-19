package server;

import server.client.Client;

public class MessageContext {
	private Client source;
	
	public MessageContext(Client source) {
		this.source = source;
	}
	
	public Client getSource() {
		return source;
	}
}
