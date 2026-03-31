package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import client.ui.Interface;
import client.ui.LoginResult;
import client.ui.UIMessage;
import client.ui.UIMessageContent;
import client.ui.UIMessageType;
import common.ErrorDefs;
import common.HeaderParseResult;
import common.MessageSerializer;
import common.MessageBus;
import common.MessageDefs;
import common.ParseResult;
import common.StreamUtils;
import common.models.messages.AnyErrorMessage;
import common.models.messages.LoginRequestMessage;
import common.models.messages.LoginResponseMessage;
import common.models.messages.TextMessage;
import server.Client;


public class Main {

	private static Thread ioThread = null;
	private static Interface ui = null;
	private static MessageBus bus = null;
	private static UserContext user = null;
	
	private static void connect(String host, int port) throws IOException {
		if(bus != null && bus.hasError() == false) {
			return;
		}
		
		try {
			Socket s = new Socket();
			s.connect(new InetSocketAddress(host, port), 5000);
			bus = new MessageBus(s);
			ui.setConnected(true);
		} catch (IOException ex) {
			ui.setConnected(false);
			ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Failed to connect at " + host + ":" + port)));
		}
		
	}
	
	private static void login(String username, String password) {
		if(username == null || password == null) {
			bus.close();
			bus = null;
			ui.setConnected(false);
			return;
		}
		
		new LoginRequestMessage(username, password).send(bus)
			.expect(MessageDefs.LOGIN_RESPONSE, LoginResponseMessage::from, (response) -> { handleLoginSuccess(username, response); })
			.error(MessageDefs.INVALID_LOGIN_ERROR, Main::handleLoginError);
	}
	
	private static void handleLoginSuccess(String username, LoginResponseMessage response) {
		user = new UserContext(username, response.getSessionId());
	}
	
	private static void handleLoginError(AnyErrorMessage error) {
		if(bus != null) {
			if(error.getSubCode() == ErrorDefs.INVALID_USERNAME_OR_PASSWORD)
				ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Login failed! Invalid username or password.")));
			else
				ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Login failed! Unknown error.")));
			
			LoginResult loginResult = ui.doLoginPopup();
			login(loginResult.Username, loginResult.Password);
		}
	}
	
	private static void run() {
		while(true) {
			if(bus == null) {
				try {
					Thread.sleep(100);
					continue;
				} catch (InterruptedException e) {
					break;
				}
			}
			
			if(bus.hasError()) { 
				bus.close();
				bus = null;
				SwingUtilities.invokeLater(() -> { ui.setConnected(false); });
				continue;
			}
			
			bus.handle();
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		ioThread = new Thread(Main::run);
		ioThread.start();
		
		ui = new Interface("Client");
		
		ui.registerOnConnect((host, port) -> {
			try {
				connect(host, Integer.parseInt(port));
				if(bus != null) {
					LoginResult loginResult = ui.doLoginPopup();
					login(loginResult.Username, loginResult.Password);
				}
				
			} catch(Exception ex) {
				throw new RuntimeException(ex); // register will catch this and handle.
			}
		});

		ui.setVisible(true);
	}

}
