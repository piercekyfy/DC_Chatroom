package server;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.net.ssl.SSLServerSocket;
import javax.swing.SwingUtilities;

import common.MessageDefs;
import common.models.TextMessage;
import common.ui.UIMessage;
import common.ui.UIUser;
import server.repositories.MessageRepository;
import server.repositories.UserRepository;
import server.ui.Interface;

public class Main {
	
	private static DemoSocketFactory socketFactory;
	private static Interface ui = null;
	private static Server server = null;
	private static Controller controller = null;
	private static MessageRepository messageRepository = new MessageRepository();
	private static UserRepository userRepository = new UserRepository();
	
	private static void start(int port) {
		if(server != null)
			return;
		
		try {
			SSLServerSocket socket = socketFactory.getSocket(port);
			controller = new Controller(messageRepository, userRepository);
			server = new Server(socket, controller);
			ui.setStarted(true);
			ui.appendSystemMessage("Started at port:" + port);	
		} catch (IOException ex) {
			ui.setStarted(false);
			ui.appendSystemMessage("Failed to start server: " + ex.getMessage());
		} catch (InvalidRouteException ex) {
			ui.setStarted(false);
			ui.appendSystemMessage("Failed to configure routes for specified controller.");
		} 
	}
	
	private static void stop() {
		server.close();
		server = null;
		ui.setStarted(false);
		ui.appendSystemMessage("Stopped...");
	}
	
	/**
	 * 
	 * @param args - full path to .jks key-store , key-store password
	 */
	public static void main(String[] args) {
		
		try {
			socketFactory = new DemoSocketFactory(args[0], args[1]);
		} catch(Exception ex) {
			System.out.println("Failed to instantiate socket factory, found no valid key-store file at " + args[0] + " or password supplied was incorrect.");
			return;
		}

		ui = new Interface("Host");
		
		ui.registerOnToggleStart((port) -> {
			try {
				int portInt = Integer.parseInt(port);
				if(server == null)
					start(portInt);
				else
					stop();
			} catch(Exception ex) {
				ui.appendSystemMessage("Unexpected error in start-up callback.");
				server.close();
				server = null;
			}
		});
		
		messageRepository.registerOnMessageCreated((message) -> {
			SwingUtilities.invokeLater(() -> {
				ui.appendMessage(message);
			});
		});
		
		userRepository.registerOnSessionCreated((user) -> {
			SwingUtilities.invokeLater(() -> {
				ui.drawUsers(userRepository.getAll());
			});
		});
		
		userRepository.registerOnSessionUpdated((user) -> {
			SwingUtilities.invokeLater(() -> {
				ui.drawUsers(userRepository.getAll());
			});
		});
		
		userRepository.registerOnSessionRemoved((users) -> {
			SwingUtilities.invokeLater(() -> {
				ui.drawUsers(userRepository.getAll());
			});
		});
		
		ui.setVisible(true);
	}
}
