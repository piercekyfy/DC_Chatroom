package server;

import common.models.UserSession;
import common.models.messages.Message;

public class MessageContext {
	private Client source;
	
	public MessageContext(Client source) {
		this.source = source;
	}
	
	public void reply(Message<?> message) {
		source.sendMessage(message);
	}
	
	public Client getSource() {
		return source;
	}
	
	public String getUsername() {
		return source.getSession().getUsername();
	}
	
	public boolean isLoggedIn() {
		return source.getSession() != null;
	}
	
	public UserSession getSession() {
		return source.getSession();
	}
}
