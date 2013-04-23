package com.ocean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Caribbean {

	private static final int SRV_PORT = 3333;
	
	private ServerSocket serverSocket;
	private Socket connection = null;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		
		Caribbean caribbean;
		try {
			caribbean = new Caribbean();
			caribbean.startWatchingOcean();
		} catch (IOException ioException) {
			// TODO Auto-generated catch block
			ioException.printStackTrace();
		}

	}
	
	public Caribbean() throws IOException {
		this.serverSocket = new ServerSocket(SRV_PORT);
		System.out.print("LISTENING...\n");
		
	}

	//Loop 
	private void startWatchingOcean() throws IOException {
		while(true) {
			this.run();
		}
	}

	private void run() throws IOException {	
			this.connection = this.serverSocket.accept();
			System.out.print("connection received from: " 
					+ connection.getInetAddress());
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());	
			System.out.println("input: "+input);
	}

}
