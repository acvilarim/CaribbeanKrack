package com.bean;

import java.io.PrintStream;
import java.net.Socket;

public class ClientDetails {
	
	public PrintStream clientOutput;
	
	private Socket connection;
	
	public ClientDetails(Socket s) {
		connection = s;		
	}

	public Socket getConnection() {
		return connection;
	}
}
