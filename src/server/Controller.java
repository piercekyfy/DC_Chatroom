package server;

import common.MessageBuilder;
import common.MessageDefs;
import common.models.User;

public class Controller {
	
	@Route(code = MessageDefs.BROADCAST)
	public void HandleBroadcast(MessageContext context, int senderId, String content) {
		
		System.out.println("CONTROLLER");
		
		System.out.println(context.getSource() + " receieved Message from " + senderId + " with content: " + content);
	
		context.getSource().sendMessage(new MessageBuilder().setCode(MessageDefs.RESPONSE_SUCCESS));
	}
}
