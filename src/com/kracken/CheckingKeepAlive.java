package com.kracken;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;

import com.bean.ClientDetails;
import com.bean.MessagesConstants;
import com.ocean.Server;

public class CheckingKeepAlive extends Thread {

	public static Vector<ClientDetails> clients;
	public CheckingKeepAlive() {
		clients = new Vector<ClientDetails>();
		
	}
	
	public void setClients(Vector<ClientDetails> clients) {
		this.clients = clients;
		
	}
	
	@Override
	public void run() {
		while (true) {
			System.out.println("the walking dead....");
			Vector<ClientDetails> clientsRemoved = new Vector<ClientDetails>();
			for (ClientDetails client : clients) {
				if (client.isDead()) {
					//nao podemos remover um client enquanto percorremos o array de clients.
					clientsRemoved.add(client);
				}
			}
			clients.removeAll(clientsRemoved);
			try {
				CheckingKeepAlive.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
