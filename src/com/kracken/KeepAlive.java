package com.kracken;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import com.bean.MessagesConstants;

public class KeepAlive extends Thread {

	private Socket connection;
	public KeepAlive(Socket connection) {
		this.connection = connection;
	}
	
	@Override
	public void run() {
		while (true) {
			PrintStream saida;
			try {
				saida = new PrintStream(connection.getOutputStream());
				saida.println(MessagesConstants.KEEP_ALIVE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				KeepAlive.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}



}
