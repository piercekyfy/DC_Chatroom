package server;

import java.time.LocalDateTime;
import java.util.List;

import common.ErrorDefs;
import common.MessageSerializer;
import common.MessageDefs;
import common.StreamUtils;
import common.models.DownloadResponsePreamble;
import common.models.User;
import common.models.UserSession;
import common.models.messages.AnyErrorMessage;
import common.models.messages.LoginResponseMessage;
import common.models.messages.TextMessage;

public class Controller {
	
	private MessageRepository messageRepository;
	private UserRepository userRepository;
	
	public Controller(MessageRepository messageRepository, UserRepository userRepository) {
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
	}
	
	@Route(code = MessageDefs.BROADCAST)
	public void HandleBroadcast(MessageContext context, int senderId, String timestampStr, String content) {		
		LocalDateTime dateTime = StreamUtils.dateTimeFromString(timestampStr.trim());
		
		System.out.println(context.getSource() + " receieved Message from " + senderId + " at " + dateTime + " with content: " + content);
	
		messageRepository.putOne(new TextMessage(MessageDefs.BROADCAST, senderId, dateTime, content));

		context.getSource().sendMessage(new TextMessage(MessageDefs.RESPONSE_SUCCESS, -1, LocalDateTime.now(), "Send!").serialize());
	}
	
	@Route(code = MessageDefs.DOWNLOAD_MESSAGE)
	public void HandleDownloadMessage(MessageContext context, int count) {
		// TODO: right now it just downloads everything, assuming count = -1
		
		List<TextMessage> messages = messageRepository.getAll();
		
		context.getSource().sendMessage(DownloadResponsePreamble.GetBuilder(new DownloadResponsePreamble(messages.size())));
		
		for(TextMessage message : messages) {
			context.getSource().sendMessage(message.serialize());
		}
	}
	
	@Route(code = MessageDefs.LOGIN_REQUEST)
	public void handleLoginRequest(MessageContext context, String username, String password, int sessionId) {
		// Would check username/password here
		
		UserSession existing = null;
		if(sessionId != UserSession.NO_SESSION_ID)
			existing = userRepository.getOne(sessionId);
		
		if(existing == null)
			existing = userRepository.putOne(username);
		else
			existing.setUpdated();
		
		context.getSource().setSessionId(existing);
		
		context.reply(new LoginResponseMessage(existing.getSessionId()).serialize());
	}
}
