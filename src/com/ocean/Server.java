package com.ocean;
import java.io.*;
import java.net.*;
import java.util.*;

import com.bean.ClientDetails;
import com.bean.CrackJob;

public class Server extends Thread {
	
	// Parte que controla as conex�es por meio de threads.
	// Note que a instancia��o est� no main.
	private static Vector<ClientDetails> clients;
	private static Vector<CrackJob> jobs;
	
	public static void main(String args[]) {
		// instancia o vetor de clientes conectados
		clients = new Vector<ClientDetails>();
		jobs = new Vector<CrackJob>();
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
				clients.add(details);
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
	private ClientDetails clientConnected;
	// construtor que recebe o socket deste cliente
	public Server(ClientDetails s) {
		clientConnected = s;
	}
	private boolean connected;
	
	// execu��o da thread
	public void run() {
		try {
			System.out.println("\n ENTRANDO NO LOOP DA THREAD");
			connected = true;
			// Loop principal: esperando por alguma string do cliente.
			while(connected) {
				// Quando recebe, envia a todos os conectados at� que o
				// cliente envie linha em branco.
				String linha = clientConnected.read();
				System.out.println("Mensagem: "+linha);
				if (linha.equals("SAIR")) {
					connected = false;
				} else {
					validaEntrada(linha);
				}
			}
			clients.remove(clientConnected);
			clientConnected.getConnection().close();
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
	private static final int MESSAGE_CHAR = 1;
	private static final String SEPARATOR = ":";
	
	private void validaEntrada(String linha) {
		
		String[] message = linha.split(SEPARATOR);
		
		if (message[COMMAND_CHAR].equals(CRACK)) {
			
			jobs.add(new CrackJob(clientConnected, message[MESSAGE_CHAR]));
			clientConnected.sendMessage("OnQueue");
			
			//- Enviar mensagem de JOB adicionado			
			//- quais maquinas conectadas (Ja sabemos)s
			//- distribuir tarefas
			System.out.println("CRACK");
		} else  if (message[COMMAND_CHAR].equals(QUEUE)) {
			//pesquisar na lista de jobs a posição do mesmo
			System.out.println("QUEUE");
		} else  if (message[COMMAND_CHAR].equals(STATS)) {
			//pesquisar na lista de jobs do CrackJob quanto já foi feito e enviar o percentual
			System.out.println("STATS");
		} else  if (message[COMMAND_CHAR].equals(JOB_RESULT)) {
			//setar na lista de jobs do crackjob o resultador daquele job.
			System.out.println("JOB_RESULT");
		} else  if (message[COMMAND_CHAR].equals(SEND_JOB)) {
			//enviar um job da lista de jobs do crackjob.
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
