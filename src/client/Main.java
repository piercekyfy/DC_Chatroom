package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import common.HeaderParseResult;
import common.MessageBuilder;
import common.ParseResult;
import common.StreamUtils;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Socket s = new Socket();
		
		s.connect(new InetSocketAddress("127.0.0.1", 6540));;
		
		OutputStream out = s.getOutputStream();
		
		System.out.println("Send 1");
		
		MessageBuilder builder = new MessageBuilder();
		builder.setCode(1);
		builder.appendContentInt(11);
		builder.appendContentString("Hello World!");
		
		out.write(builder.build());
		
		InputStream in = s.getInputStream();
		
		HeaderParseResult headerResult = StreamUtils.readHeader(in);
		
		byte[] buffer = new byte[1024];
		in.read(buffer);
		
		System.out.println("ReplyCode:" + headerResult.getValue().getCode());
		
		//System.out.println("ErrorDef: " + StreamUtils.parseInt(buffer, 0).getValue());
		//System.out.println("InvalidArg: " + StreamUtils.parseInt(buffer, 4).getValue());
	}

}
