package server;

import java.time.LocalDateTime;
import java.util.List;

import common.MessageBuilder;
import common.MessageDefs;
import common.StreamUtils;
import common.models.DownloadResponsePreamble;
import common.models.TextMessage;
import common.models.User;

public class Controller {
	
	private MessageRepository repository;
	
	public Controller(MessageRepository repository) {
		this.repository = repository;
	}
	
	@Route(code = MessageDefs.BROADCAST)
	public void HandleBroadcast(MessageContext context, int senderId, String timestampStr, String content) {		
		LocalDateTime dateTime = StreamUtils.dateTimeFromString(timestampStr.trim());
		
		System.out.println(context.getSource() + " receieved Message from " + senderId + " at " + dateTime + " with content: " + content);
	
		repository.putOne(new TextMessage(MessageDefs.BROADCAST, senderId, dateTime, content));
		
		context.getSource().sendMessage(new MessageBuilder().setCode(MessageDefs.RESPONSE_SUCCESS).appendContentString("blah"));
	}
	
	@Route(code = MessageDefs.DOWNLOAD_MESSAGE)
	public void HandleDownloadMessage(MessageContext context, int count) {
		// TODO: right now it just downloads everything, assuming count = -1
		
		List<TextMessage> messages = repository.getAll();
		
		context.getSource().sendMessage(DownloadResponsePreamble.GetBuilder(new DownloadResponsePreamble(messages.size())));
		
		for(TextMessage message : messages) {
			context.getSource().sendMessage(TextMessage.GetBuilder(message));
		}
	}
}
