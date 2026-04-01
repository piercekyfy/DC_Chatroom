package server;

import java.io.IOException;
import java.time.LocalDateTime;

import common.MessageDefs;
import common.models.messages.TextMessage;
import common.ui.UIMessage;
import common.ui.UIMessageContent;
import common.ui.UIMessageType;
import ui.Interface;

public class Main {
	
	private static Interface ui = null;
	private static Server server = null;
	private static Controller controller = null;
	private static MessageRepository messageRepository = new MessageRepository();
	private static UserRepository userRepository = new UserRepository();
	
	private static void start(int port) {
		if(server != null)
			return;
		
		try {
			controller = new Controller(messageRepository, userRepository);
			server = new Server(port, controller);
			ui.setStarted(true);
			ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Started at port:" + port)));	
		} catch (IOException ex) {
			ui.setStarted(false);
			ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Failed to start server: " + ex.getMessage())));
		} catch (InvalidRouteException ex) {
			ui.setStarted(false);
			ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Failed to configure routes for specified controller.")));
		} 
	}
	
	private static void stop() {
		server.close();
		server = null;
		ui.setStarted(false);
		ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Stopped...")));	
	}
	
	public static void main(String[] args) {
		// Test Data
		messageRepository.putOne(new TextMessage(MessageDefs.BROADCAST, 11, LocalDateTime.now(), "Hello1."));
		messageRepository.putOne(new TextMessage(MessageDefs.BROADCAST, 12, LocalDateTime.now(), "Hello2."));
		messageRepository.putOne(new TextMessage(MessageDefs.BROADCAST, 13, LocalDateTime.now(), "Hello3."));
		
		ui = new Interface("Host");
		
		ui.registerOnToggleStart((port) -> {
			try {
				int portInt = Integer.parseInt(port);
				if(server == null)
					start(portInt);
				else
					stop();
			} catch(Exception ex) {
				ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Unexpected error in start-up callback.")));
				server.close();
				server = null;
			}
		});
		
		ui.setVisible(true);
	}
}
