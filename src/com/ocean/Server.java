package com.ocean;
import java.io.*;
import java.net.*;
import java.util.*;

import utils.Md5Generator;

import com.bean.ClientDetails;
import com.bean.CrackJob;
import com.bean.MessagesConstants;

public class Server extends Thread {
	
	// Parte que controla as conex�es por meio de threads.
	// Note que a instancia��o est� no main.
	private static Vector<ClientDetails> clients;
	private static Vector<ClientDetails> backups;
	private static Vector<CrackJob> jobs;
	
	//PREENCHER O HARDCODE AQUI, PELO AMOR DE JESUS CRISTO! 
	private static String backupServerAddress = "/127.0.0.1";
	private static int jobOnProcess;
	
	public static void main(String args[]) {
		// instancia o vetor de clientes conectados
		clients = new Vector<ClientDetails>();
		backups = new Vector<ClientDetails>();
		jobs = new Vector<CrackJob>();
		jobOnProcess = 0;
		try {
			// criando um socket que fica escutando a porta 2222.
			ServerSocket s = new ServerSocket(2222);
			// Loop principal.
			System.out.println("SERVER INICIADO em "+s.getInetAddress()+":"+s.getLocalPort());
			while (true) {
				Socket connection = s.accept();
				
				ClientDetails details = new ClientDetails(connection);
				System.out.println(connection.getInetAddress().toString());
				System.out.println(backupServerAddress);
				/*if (connection.getInetAddress().toString().equals(backupServerAddress))
				{
					backups.add(details);
					System.out.println("BACKUP SERVER");
				}
				else
				{*/
					clients.add(details);
				//}
				System.out.println("Novo Client connectado!");
				
				Thread t = new Server(details);
				t.start();
			}
		}
		catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}
	
	/**
	 * Iniciando threads no servidor
	 */
	
	private ClientDetails clientConnected;
	
	public Server(ClientDetails s) {
		clientConnected = s;
	}
	
	private boolean connected;
	
	public void run() {
		try {
			connected = true;
			while(connected) {
				String linha = clientConnected.read();
				if (linha.equals("SAIR")) {
					connected = false;
				} else {
					validaEntrada(linha);
				}
				jobs.get(jobOnProcess).printStatus(jobOnProcess);
			}
			clients.remove(clientConnected);
			clientConnected.getConnection().close();
		}
		catch (IOException e) {
			System.out.println("IOException: " + e);
		}

	}
	
	private void validaEntrada(String linha) {
		
		String[] message = linha.split(MessagesConstants.SEPARATOR);
		if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.CRACK)) {
			CrackJob job = new CrackJob(clientConnected, message[MessagesConstants.MESSAGE_CHAR]);
			if (jobs.size() == 0) {
				job.startJob();
			}
			jobs.add(job);
			clientConnected.sendMessage(getJobPosition(job));
			//TODO: ENVIAR OS JOBS SERIALIZADOS PARA O BACKUP
			try{
				FileOutputStream fo = new FileOutputStream("test.ser");
				ObjectOutputStream oo = new ObjectOutputStream(fo);
				//System.out.println(jobs.get(i));
				oo.writeObject(jobs); // serializo objeto cat
				oo.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			//- Enviar mensagem de JOB adicionado			
			//- quais maquinas conectadas (Ja sabemos)s
			//- distribuir tarefas
			
			for (ClientDetails client : clients) {
				int[] ids = new int[2];
				CrackJob jobToCrack = jobs.get(jobOnProcess);
				ids = jobToCrack.getNextJobs(client);
				String msg = MessagesConstants.CRACK+
						MessagesConstants.SEPARATOR+ids[0]+
						MessagesConstants.SEPARATOR+ids[1]+
						MessagesConstants.SEPARATOR+jobToCrack.getHash();
				client.sendMessage(msg);
				//TODO: ENVIAR ENDEREÇO DO SERVIDOR BACKUP PROS CLIENTS
			}
			
			System.out.println("CRACK");
		} else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.QUEUE)) {
			//pesquisar na lista de jobs a posição do mesmo
			System.out.println("QUEUE");
		} else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.STATS)) {
			//pesquisar na lista de jobs do CrackJob quanto já foi feito e enviar o percentual
			System.out.println("STATS");
		} else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.JOB_RESULT)) {
			String probablePassword = message[MessagesConstants.MESSAGE_CHAR];
			if (probablePassword.equals(MessagesConstants.NOT_FOUND)) {
				jobs.get(jobOnProcess).completeJobsFrom(clientConnected);
			} else {
				if (jobs.get(jobOnProcess).tryToEndJob(clientConnected, probablePassword)) {
					jobOnProcess++;
				} else {
					//Enviar novamente a mesma lista de jobs.
					//jobs.get(jobOnProcess).restartJobs(clientConnected);
				}
			}
			
		} else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.SEND_JOB)) {
			//enviar um job da lista de jobs do crackjob.
			System.out.println("SEND_JOB");
		}
	}

	private String getJobPosition(CrackJob job) {
		int position = jobs.indexOf(job);
		int queue = position - jobOnProcess; 
		return MessagesConstants.QUEUE+MessagesConstants.SEPARATOR+queue;
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
