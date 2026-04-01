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
import common.ErrorDefs;
import common.HeaderParseResult;
import common.MessageSerializer;
import common.MessageBus;
import common.MessageDefs;
import common.ParseResult;
import common.StreamUtils;
import common.models.UserSession;
import common.models.messages.AnyErrorMessage;
import common.models.messages.LoginRequestMessage;
import common.models.messages.LoginResponseMessage;
import common.models.messages.TextMessage;
import common.ui.UIMessage;
import common.ui.UIMessageContent;
import common.ui.UIMessageType;
import server.Client;


public class Main {

	private static Thread ioThread = null;
	private static Interface ui = null;
	private static MessageBus bus = null;
	private static UserSession user = null;
	
	private static void connect(String host, int port) throws IOException {
		if(bus != null && bus.hasError() == false) {
			return;
		}
		
		try {
			Socket s = new Socket();
			s.setSoTimeout(100);
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
		
		new LoginRequestMessage(username, password).asTask()
			.expect(MessageDefs.LOGIN_RESPONSE, LoginResponseMessage::from, (response) -> { handleLoginSuccess(username, response); })
			.error(MessageDefs.INVALID_LOGIN_ERROR, Main::handleLoginError)
			.send(bus);
	}
	
	private static void handleLoginSuccess(String username, LoginResponseMessage response) {
		user = new UserSession(username, response.getSessionId());
		SwingUtilities.invokeLater(() -> {
			ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Logged in as: " + user.getUsername() + "(" + user.getSessionId() + ").")));
		});
	}
	
	private static void handleLoginError(AnyErrorMessage error) {
		if(bus != null) {
			if(error.getSubCode() == ErrorDefs.INVALID_USERNAME_OR_PASSWORD)
				SwingUtilities.invokeLater(() -> {
					ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Login failed! Invalid username or password.")));
				});
			else
				SwingUtilities.invokeLater(() -> {
					ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Login failed! Unknown error.")));
				});

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
			} catch(Exception ex) {
				ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Unexpected error while connecting: " + ex.getMessage())));
				bus = null;
				ui.setConnected(false);
				return;
			}
			
			try {
				if(bus != null) {
					LoginResult loginResult = ui.doLoginPopup();
					login(loginResult.Username, loginResult.Password);
				}
			} catch(Exception ex) {
				SwingUtilities.invokeLater(() -> {
					ui.append(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Unexpected error while logging in: " + ex.getMessage())));
					ui.setConnected(false);
				});
				bus = null;
				
				return;
			}
		});

		ui.setVisible(true);
	}

}
