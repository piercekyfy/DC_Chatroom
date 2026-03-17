package server;

import common.MessageDefs;
import common.models.User;

public class Controller {
	
	@Route(code = MessageDefs.BROADCAST)
	public void HandleBroadcast(int senderId, String content) {
		
		System.out.println("CONTROLLER");
		
		System.out.println("Receieved Message from " + senderId + " with content: " + content);
	}
}
