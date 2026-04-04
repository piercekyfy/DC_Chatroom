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
import common.MessageTask;
import common.MessageBus;
import common.MessageDefs;
import common.ParseResult;
import common.StreamUtils;
import common.models.TextMessage;
import common.models.UserSession;
import common.models.requests.CheckMessagesRequest;
import common.models.requests.DownloadMessageRequest;
import common.models.requests.LoginRequest;
import common.models.requests.SendTextMessageRequest;
import common.models.responses.GenericErrorResponse;
import common.models.responses.LoginResponse;
import common.models.responses.MessageContentResponse;
import common.models.responses.MessageIdResponse;
import common.ui.UIMessage;
import server.Client;


public class Main {

	private static Thread ioThread = null;
	private static Interface ui = null;
	private static MessageBus bus = null;
	private static UserSession user = null;

	private static String lastHost = "";
	private static int lastPort = 0;
	private static String lastPass = "";
	
	private static final int POLL_MESSAGES_MS = 1000;
	
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
			lastHost = host;
			lastPort = port;
		} catch (IOException ex) {
			ui.setConnected(false);
			ui.appendSystemMessage("Failed to connect at " + host + ":" + port);
		}
	}
	
	private static void disconnect() {
		ui.setConnected(false);
		ui.appendSystemMessage("Disconnected...");
		
		if(bus != null) {
			bus.close();
		}
		
		bus = null;
	}
	
	private static void login(String username, String password) {
		if(username == null || password == null) {
			bus.close();
			bus = null;
			ui.setConnected(false);
			return;
		}
		
		new LoginRequest(username, password).asTask()
			.expect(MessageDefs.LOGIN_RESPONSE, LoginResponse::from, (response) -> { handleLoginSuccess(username, password, response); })
			.error(MessageDefs.GENERIC_ERROR, Main::handleLoginError)
			.closed(Main::disconnect)
			.send(bus);
	}
	
	private static void login(UserSession session, String password) {
		if(session.getUsername() == null || password == null) {
			bus.close();
			bus = null;
			ui.setConnected(false);
			return;
		}
		
		new LoginRequest(session.getUsername(), password, session.getSessionId()).asTask()
			.expect(MessageDefs.LOGIN_RESPONSE, LoginResponse::from, (response) -> { handleLoginSuccess(session.getUsername(), password, response); })
			.error(MessageDefs.GENERIC_ERROR, Main::handleLoginError)
			.closed(Main::disconnect)
			.send(bus);
	}
	
	private static void handleLoginSuccess(String username, String password, LoginResponse response) {
		lastPass = password;
		user = new UserSession(username, response.getSessionId());
		SwingUtilities.invokeLater(() -> {
			ui.appendSystemMessage("Logged in as: " + user.getUsername() + "(" + user.getSessionId() + ").");
		});
	}
	
	private static void handleLoginError(GenericErrorResponse error) {
		if(bus != null) {
			if(error.getSubCode() == ErrorDefs.INVALID_USERNAME_OR_PASSWORD)
				SwingUtilities.invokeLater(() -> {
					ui.appendSystemMessage("Login failed! Invalid username or password.");
				});
			else if (error.getSubCode() == ErrorDefs.NO_SESSION) {
				user = null;
				SwingUtilities.invokeLater(() -> {
					ui.appendSystemMessage("Attempted to reconnect with expired session.");
				});
			}
			else
				SwingUtilities.invokeLater(() -> {
					ui.appendSystemMessage("Login failed! Unknown error.");
					
					
				});

			LoginResult loginResult = ui.doLoginPopup();
			login(loginResult.Username, loginResult.Password);
		}
	}
	
	private static void run() {
		long lastTime = System.currentTimeMillis();
		MessageTask<CheckMessagesRequest> lastCheckTask = null;

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
				if(user != null) {
					try {
						connect(lastHost, lastPort);
						login(user, lastPass);
					} catch (IOException ex) {
						bus.close();
						bus = null;
						SwingUtilities.invokeLater(() -> { ui.setConnected(false); });
					}
				} else {
					SwingUtilities.invokeLater(() -> { ui.setConnected(false); });
				}
						
				continue;
			}
			
			long now = System.currentTimeMillis();
			if (now - lastTime >= POLL_MESSAGES_MS) {
				lastTime = now;
				
				if(bus != null && user != null) {
				
		            if(lastCheckTask != null)
		            	lastCheckTask.expire();
		            
		            new CheckMessagesRequest().asTask()
		            	.dontExpireOnComplete()
		            	.expect(MessageDefs.MESSAGE_ID, MessageIdResponse::from, Main::registerNewMessage)
		            	.error(MessageDefs.GENERIC_ERROR, (err) -> { if(err.getSubCode() == ErrorDefs.NO_MESSAGES) { System.out.println("Polled, found nothing."); } })
		            	.send(bus);
		        
				}
			}
			
			bus.handle();
		}
	}
	
	private static void registerNewMessage(MessageIdResponse id) {
		if(user.isDownloaded(id.getId()))
			return;
		user.registerDownloaded(id.getId()); // Convenient to re-use user session functionality to track local downloads.
		
		new DownloadMessageRequest(id.getId()).asTask()
			.expect(MessageDefs.MESSAGE_CONTENT, MessageContentResponse::from, (content) -> {
				SwingUtilities.invokeLater(() -> {
					ui.appendMessage(new TextMessage(content.getId(), content.getSender(), content.getContent()));
				});
			})
			.send(bus);
	}
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		ioThread = new Thread(Main::run);
		ioThread.start();
		
		ui = new Interface("Client");
		
		ui.registerOnConnect((host, port) -> {
			try {
				connect(host, Integer.parseInt(port));
			} catch(Exception ex) {
				ui.appendSystemMessage("Unexpected error while connecting: " + ex.getMessage());
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
				ui.appendSystemMessage("Unexpected error while logging in: " + ex.getMessage());
				bus = null;
				ui.setConnected(false);
				return;
			}
		});
		
		ui.registerOnSend((text) -> {
			if(bus != null) {
				new SendTextMessageRequest(text).asTask()
					.expect(MessageDefs.MESSAGE_CONTENT, MessageContentResponse::from, (response) -> {
						user.registerDownloaded(response.getId());
						ui.appendMessage(new TextMessage(response.getId(), response.getSender(), response.getContent()));
					})
					.error(MessageDefs.GENERIC_ERROR, (error) -> {
						ui.appendSystemMessage("Error!: " + error);
					})
					.closed(() -> {
						ui.appendSystemMessage("Failed to send message. Reconnecting...");
					})
					.send(bus);
			}
		});

		ui.setVisible(true);
	}

}
