package com.ocean;
import java.io.*;
import java.net.*;
import java.util.*;

import utils.Md5Generator;

import com.bean.ClientDetails;
import com.bean.CrackJob;
import com.bean.MessagesConstants;
import com.ships.Client;

public class Server extends Thread {
	
	// Parte que controla as conex�es por meio de threads.
	// Note que a instancia��o est� no main.
	private static Vector<ClientDetails> clients;
	private static Vector<ClientDetails> backups;
	private static Vector<CrackJob> jobs;
	
	//PREENCHER O HARDCODE AQUI, PELO AMOR DE JESUS CRISTO! 
	private static String backupServerAddress = "192.168.1.104";
	private static String mainServerAddress = "192.168.1.95";
	private static int jobOnProcess;
	private static boolean isBackup = false;
	private static ServerSocket s;
	
	public static void main(String args[]) {
		// instancia o vetor de clientes conectados
		clients = new Vector<ClientDetails>();
		backups = new Vector<ClientDetails>();
		jobs = new Vector<CrackJob>();
		jobOnProcess = 0;
		try {
			// criando um socket que fica escutando a porta 2222.
			s = new ServerSocket(2222);
			// Loop principal.
			System.out.println("SERVER INICIADO em "+s.getInetAddress()+":"+s.getLocalPort());
			while (true) {
				Socket connection = s.accept();
				
				ClientDetails details = new ClientDetails(connection);
				System.out.println(connection.getInetAddress().toString());
				System.out.println(backupServerAddress);
				if (connection.getInetAddress().toString().equals("/"+backupServerAddress))
				{
					backups.add(details);
					System.out.println("BACKUP SERVER");
				}
				else
				{
					clients.add(details);
				}
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
			ObjectInputStream ois = 
                    new ObjectInputStream(clientConnected.getConnection().getInputStream());
			while(connected) {
				try {
					jobs = ((Vector<CrackJob>) ois.readObject());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
<<<<<<< HEAD
		
		System.out.println("recebendo: "+linha);

=======
>>>>>>> parent of 340a2d0... Server and Clients working
		if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.CRACK)) {
			CrackJob job = new CrackJob(clientConnected, message[MessagesConstants.MESSAGE_CHAR]);
			if (jobs.size() == 0) {
				job.startJob();
			}
			jobs.add(job);
			clientConnected.sendMessage(getJobPosition(job));
			//TODO: ENVIAR OS JOBS SERIALIZADOS PARA O BACKUP
			for (int i = 0; i < jobs.size(); i++)
			{
				try{
					FileOutputStream fo = new FileOutputStream("test.ser");
					ObjectOutputStream oo = new ObjectOutputStream(fo);
					System.out.println(jobs.get(i));
					oo.writeObject((CrackJob)jobs.get(i)); // serializo objeto cat
					oo.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
<<<<<<< HEAD
=======
			//- Enviar mensagem de JOB adicionado			
			//- quais maquinas conectadas (Ja sabemos)s
			//- distribuir tarefas
			
>>>>>>> parent of 340a2d0... Server and Clients working
			for (ClientDetails client : clients) {
				int[] ids = new int[2];
				CrackJob jobToCrack = jobs.get(jobOnProcess);
				ids = jobToCrack.getNextJobs(client);
				String msg = MessagesConstants.CRACK+
						MessagesConstants.SEPARATOR+ids[0]+
						MessagesConstants.SEPARATOR+ids[1]+
						MessagesConstants.SEPARATOR+jobToCrack.getHash();
				client.sendMessage(msg);
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
		else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.MIRROR_MAIN_SERVER)) {
			//TODO:PREENCHER O JOBONPROCESS PARA O BACKUP
		}
		
		try{
			FileOutputStream fo = new FileOutputStream("test.ser");
			ObjectOutputStream oo = new ObjectOutputStream(fo);
			oo.writeObject(jobs); // serializo vector de jobs
			oo.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//TODO: ENVIAR OS JOBS SERIALIZADOS PARA O BACKUP
		//TODO: ENVIAR O JOBONPROCESS PARA O BACKUP
		//TODO: ENVIAR O ENDEREÇO DO SERVIDOR DE BAKCUP PARA OS CLIENTS
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
