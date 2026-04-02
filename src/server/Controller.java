package server;

import java.time.LocalDateTime;
import java.util.List;

import common.ErrorDefs;
import common.MessageSerializer;
import common.MessageDefs;
import common.StreamUtils;
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
		
		context.getSource().requestDisconnect();
	}
}
