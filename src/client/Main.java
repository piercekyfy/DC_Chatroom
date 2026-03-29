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

import common.HeaderParseResult;
import common.MessageBuilder;
import common.MessageBus;
import common.MessageDefs;
import common.ParseResult;
import common.StreamUtils;
import common.models.messages.TextMessage;
import server.Client;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Socket s = new Socket();
		s.connect(new InetSocketAddress("127.0.0.1", 6540));;
		MessageBus bus = new MessageBus(s);
		
		System.out.println("Client Running...");
		
		TextMessage message = new TextMessage(MessageDefs.BROADCAST, 11, LocalDateTime.now(), "2!");
		message.send(bus)
			.expect(MessageDefs.RESPONSE_SUCCESS, TextMessage::from,
				(result) -> {
					System.out.println("Receieved Message from " + result.getSenderId() + " at " + result.getTimestamp() + " with content: " + result.getContent());
				}
			)
			.error(MessageDefs.INVALID_CONTENT_ERROR, (error) -> {
				System.out.println("Invalid content error: Subcode: " + error.getSubCode() + " SourceCode: " + error.getSourceCode());
			});
		
		while(true) {
			if(bus.hasError())
				break;
			
			bus.handle();
			

			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	}

}
