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
	private static Vector<CrackJob> jobs;
	private static int jobOnProcess;
	
	public static void main(String args[]) {
		
		// instancia o vetor de clientes conectados
		clients = new Vector<ClientDetails>();
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
				clients.add(details);
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
				//if (linha==null)
				if (linha.equals("SAIR")) {
					connected = false;
				} else {
					validaEntrada(linha);
					if (jobs.size() > jobOnProcess) {
						//jobs.get(jobOnProcess).printStatus(jobOnProcess);
					}
				}
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

		System.out.println("recebendo: "+linha);
		if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.CRACK)) {
			System.out.println("CRACK");
			CrackJob job = new CrackJob(clientConnected, message[MessagesConstants.MESSAGE_CHAR]);
			jobs.add(job);
			job.setId(jobs.indexOf(job,jobOnProcess));
			//TODO: ENVIAR OS JOBS SERIALIZADOS PARA O BACKUP
//			for (int i = 0; i < jobs.size(); i++)
//			{
//				try{
//					FileOutputStream fo = new FileOutputStream("test.ser");
//					ObjectOutputStream oo = new ObjectOutputStream(fo);
//					System.out.println(jobs.get(i));
//					oo.writeObject((CrackJob)jobs.get(i)); // serializo objeto cat
//					oo.close();
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
			
			//- Enviar mensagem de JOB adicionado			
			//- quais maquinas conectadas (Ja sabemos)s
			
			for (ClientDetails client : clients) {
				client.sendMessage(getJobPosition(job));
			}
			
		} else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.QUEUE)) {
			//pesquisar na lista de jobs a posição do mesmo
			System.out.println("QUEUE");
		} else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.STATS)) {
			//pesquisar na lista de jobs do CrackJob quanto já foi feito e enviar o percentual
			System.out.println("STATS");
		} else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.JOB_RESULT)) {
			String probablePassword = message[MessagesConstants.MESSAGE_CHAR];
			int jobId = Integer.parseInt(message[MessagesConstants.JOB_ID]);
			if (probablePassword.equals(MessagesConstants.NOT_FOUND)) {
				System.out.println("JOB_RESULT:NOT_FOUND");
				jobs.get(jobId).completeJobsFrom(clientConnected);
			} else {
				System.out.println("JOB_RESULT:FOUND");
				if (jobs.get(jobId).tryToEndJob(clientConnected, probablePassword)) {
					jobOnProcess++;
					if (jobs.size() > jobOnProcess) {
						jobs.get(jobOnProcess).startJob();
					}
				} else {
					//Enviar novamente a mesma lista de jobs.
					//jobs.get(jobOnProcess).restartJobs(clientConnected);
				}
			}
			
		} else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.SEND_JOB)) {
			//enviar um job da lista de jobs do crackjob.
			System.out.println("SEND_JOB");
			if (jobs.size() > jobOnProcess) {
				if (jobs.get(jobOnProcess).isWaiting()) {
					jobs.get(jobOnProcess).startJob();
				}
				if (jobs.get(jobOnProcess).isProcessing()) { 
					int[] ids = new int[2];
					CrackJob jobToCrack = jobs.get(jobOnProcess);
					ids = jobToCrack.getNextJobs(clientConnected);
					String msg = MessagesConstants.CRACK+
							MessagesConstants.SEPARATOR+ids[0]+
							MessagesConstants.SEPARATOR+jobToCrack.id+
							MessagesConstants.SEPARATOR+ids[1]+
							MessagesConstants.SEPARATOR+jobToCrack.getHash();
					clientConnected.sendMessage(msg);
				}
			}
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
