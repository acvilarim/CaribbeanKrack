package com.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;

public class ClientDetails {
	
	public PrintStream clientOutput;
	
	private Socket connection;
	
	private long lastKeepAlive;
	
	private int status;
	private static final int IDLE = 0;
	private static final int BUSY = 1;
	
	PrintStream output;
	BufferedReader input;
	
	public ClientDetails(Socket s) throws IOException {
		connection = s;		
		status = IDLE;
		output = new PrintStream(connection.getOutputStream());
		input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	}
	
	public void sendMessage(String str) {
		output.println(str);
	}
	
	public String read() throws IOException {
		return input.readLine();
	}
	
	public Socket getConnection() {
		return connection;
	}
	
	public void sendJob() {
		
	}
	
	public boolean isDead() {
		if (System.currentTimeMillis() > (lastKeepAlive+20000)) {
			try {
				System.out.println(connection.getInetAddress()+" Desconectado");
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false; 
	}
	
	public void keepAlive() {
		lastKeepAlive = System.currentTimeMillis();
		System.out.println(connection.getInetAddress()+" KeepingAlive");
	}
}
