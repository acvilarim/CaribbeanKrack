package com.ocean;
import java.io.*;
import java.net.*;
import java.util.*;

import com.bean.ClientDetails;

public class Server extends Thread {
	
	// Parte que controla as conex�es por meio de threads.
	// Note que a instancia��o est� no main.
	private static Vector<ClientDetails> clientes;
	
	public static void main(String args[]) {
		// instancia o vetor de clientes conectados
		clientes = new Vector<ClientDetails>();
		try {
			// criando um socket que fica escutando a porta 2222.
			ServerSocket s = new ServerSocket(2222);
			// Loop principal.
			while (true) {
				// aguarda algum cliente se conectar. A execu��o do
				// servidor fica bloqueada na chamada do m�todo accept da
				// classe ServerSocket. Quando algum cliente se conectar
				// ao servidor, o m�todo desbloqueia e retorna com um
				// objeto da classe Socket, que � porta da comunica��o.
				System.out.print("Esperando alguem se conectar...");
				Socket connection = s.accept();
				System.out.println(" Conectou!");
				ClientDetails details = new ClientDetails(connection);
				clientes.add(details);
				// cria uma nova thread para tratar essa conex�o
				Thread t = new Server(details);
				t.start();
				// voltando ao loop, esperando mais algu�m se conectar.
			}
		}
		catch (IOException e) {
			// caso ocorra alguma excess�o de E/S, mostre qual foi.
			System.out.println("IOException: " + e);
		}

	}
	
	// socket deste cliente
	private ClientDetails details;
	// construtor que recebe o socket deste cliente
	public Server(ClientDetails s) {
		details = s;
	}
	private boolean connected;
	
	// execu��o da thread
	public void run() {
		try {
			System.out.println("\n ENTRANDO NO LOOP DA THREAD");
			// objetos que permitem controlar fluxo de comunica��o
			BufferedReader entrada = new BufferedReader(new
					InputStreamReader(details.getConnection().getInputStream()));
			PrintStream saida = new 
					PrintStream(details.getConnection().getOutputStream());
			connected = true;
			// Loop principal: esperando por alguma string do cliente.
			while(connected) {
				// Quando recebe, envia a todos os conectados at� que o
				// cliente envie linha em branco.
				String linha = entrada.readLine();
				System.out.println("Mensagem: "+linha);
				if (linha.equals("SAIR")) {
					connected = false;
				} else {
					validaEntrada(linha);
				}
				
			}
			clientes.remove(details);
			details.getConnection().close();
		}
		catch (IOException e) {
			// Caso ocorra alguma excess�o de E/S, mostre qual foi.
			System.out.println("IOException: " + e);
		}
	}
	
	private static final String CRACK = "C";
	private static final String QUEUE = "Q";
	private static final String STATS = "S";
	private static final String JOB_RESULT = "R";
	private static final String SEND_JOB = "J";
	
	//"C:ASHOOAFSOPMknaondaoinoiuN"
	//"Q:ASHOOAFSOPMknaondaoinoiuN"
	
	private static final int COMMAND_CHAR = 0;
	private static final String SEPARATOR = ":";
	
	private void validaEntrada(String linha) {
		
		String[] message = linha.split(SEPARATOR);
		//String command = linha.substring(COMMAND_CHAR);
		
		if (message[COMMAND_CHAR].equals(CRACK)) {
			System.out.println("CRACK");
		} else  if (message[COMMAND_CHAR].equals(QUEUE)) {
			System.out.println("QUEUE");
		} else  if (message[COMMAND_CHAR].equals(STATS)) {
			System.out.println("STATS");
		} else  if (message[COMMAND_CHAR].equals(JOB_RESULT)) {
			System.out.println("JOB_RESULT");
		} else  if (message[COMMAND_CHAR].equals(SEND_JOB)) {
			System.out.println("SEND_JOB");
		}
		
	}
	
	
	
	// enviar uma mensagem para todos, menos para o pr�prio
//	public void sendToAll(PrintStream saida, String acao,
//			String linha) throws IOException {
//		Enumeration e = clientes.elements();
//		while (e.hasMoreElements()) {
//			// obt�m o fluxo de sa�da de um dos clientes
//			PrintStream chat = (PrintStream) e.nextElement();
//			// envia para todos, menos para o pr�prio usu�rio
//			if (chat != saida) {chat.println(meuNome + acao + linha);}
//		}
//	}


}
