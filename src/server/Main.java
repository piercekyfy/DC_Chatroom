package server;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException, InvalidRouteException, InterruptedException {
		Server s = new Server(6540);
		
		System.out.println("Running");
		
		s.Join();
	}

}
