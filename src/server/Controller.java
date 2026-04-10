package server;

import java.time.LocalDateTime;
import java.util.List;

import common.ErrorDefs;
import common.MessageSerializer;
import common.MessageDefs;
import common.StreamUtils;
import common.models.TextMessage;
import common.models.User;
import common.models.UserSession;
import common.models.responses.*;
import server.repositories.MessageRepository;
import server.repositories.UserRepository;

public class Controller {
	
	private MessageRepository messageRepository;
	private UserRepository userRepository;
	
	public Controller(MessageRepository messageRepository, UserRepository userRepository) {
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
	}
	
	private boolean requireSession(MessageContext context, int sourceCode) {
		if(context.getSession() == null) {
			context.reply(new GenericErrorResponse(ErrorDefs.NO_SESSION, sourceCode));
			return false;
		}
		return true;
	}
	
	// Login
	
	@Route(code = MessageDefs.LOGIN_REQUEST)
	public void HandleLogin(MessageContext context, String username, String password, int sessionId) {
		UserSession existing = null;
		if(sessionId != UserSession.NO_SESSION_ID) {
			existing = userRepository.getOne(sessionId);
			if(existing == null || existing.isExpired()) {
				context.reply(new GenericErrorResponse(ErrorDefs.NO_SESSION, MessageDefs.LOGIN_REQUEST));
				return;
			} else {
				existing.setUpdated();
			}
		}
		
		if(existing == null)
			existing = userRepository.putOne(username);
		
		context.getSource().setSessionId(existing);
		
		context.reply(new LoginResponse(existing.getSessionId()));
	}
	
	@Route(code = MessageDefs.LOGOUT_REQUEST)
	public void HandleLogout(MessageContext context) {
		if(!requireSession(context, MessageDefs.LOGOUT_REQUEST))
			return;
		
		userRepository.removeSessions(context.getSession().getUsername());
		
		context.reply(new DisconnectedErrorResponse(ErrorDefs.NONE, MessageDefs.LOGOUT_REQUEST));
		context.getSource().requestDisconnect();
	}
	
	
	@Route(code = MessageDefs.CHECK_MESSAGES)
	public void HandleCheckRequest(MessageContext context) {
		if(!requireSession(context, MessageDefs.CHECK_MESSAGES))
			return;
		
		int replies = 0;
		for(TextMessage message : messageRepository.getAll()) {
			if(context.getSession().isDownloaded(message.getId()))
				continue;
			context.reply(new MessageIdResponse(message.getId()));
			replies++;
		}
		
		if(replies == 0) {
			context.reply(new GenericErrorResponse(ErrorDefs.NO_MESSAGES, MessageDefs.CHECK_MESSAGES));
		}
	}
	
	@Route(code = MessageDefs.DOWNLOAD_MESSAGE)
	public void HandleDownloadRequest(MessageContext context, int id) {
		TextMessage message = messageRepository.getOne(id);
		
		if(message == null) {
			context.reply(new GenericErrorResponse(ErrorDefs.NO_MESSAGES, MessageDefs.DOWNLOAD_MESSAGE));
			return;
		}
		
		if(requireSession(context, MessageDefs.CHECK_MESSAGES)) {
			context.getSession().registerDownloaded(message.getId());
		}
		
		context.reply(new MessageContentResponse(message.getId(), message.getSender(), message.getContent()));
	}
	
	@Route(code = MessageDefs.SEND_TEXT_MESSAGE)
	public void HandleSendText(MessageContext context, String text) {
		if(!requireSession(context, MessageDefs.SEND_TEXT_MESSAGE))
			return;
		
		TextMessage message = messageRepository.putOne(context.getUsername(), text);
		
		context.getSession().registerDownloaded(message.getId());
		context.reply(new MessageContentResponse(message.getId(), message.getSender(), message.getContent()));
	}
}
