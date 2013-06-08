package com.ships;
import java.io.*;
import java.net.*;
import java.security.Timestamp;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import com.bean.ClientDetails;
import com.bean.MessagesConstants;

import utils.BruteIterator;
import utils.Md5Generator;

public class Client extends Thread {
	private static boolean done = false;
	
	private static char[] test1 = {'a', 'a', 'a','a'};
	private static char[] test2 = {'z', 'z','z','z'};
	private static Thread process;
	
	public static void main(String args[]) {
		try {
			Socket conexao = new Socket("127.0.0.1", 2222);
			PrintStream saida = new
					PrintStream(conexao.getOutputStream());
			BufferedReader teclado =
					new BufferedReader(new InputStreamReader(System.in));
			process = new Client(conexao);
			process.start();
			
			String linha;
			while (true) {
				System.out.print("> ");
				linha = teclado.readLine();
				if (done) {
					break;
				}
				System.out.println("Mensagem Enviada: "+linha);
				saida.println(linha);
			}
		}
		catch (IOException e) {
			// Caso ocorra alguma excessâ€¹o de E/S, mostre qual foi.
			System.out.println("IOException: " + e);
		}
	}
	// parte que controla a recepï¿½â€¹o de mensagens deste cliente
	private Socket conexao;
	// construtor que recebe o socket deste cliente

	public Client(Socket s) {
		conexao = s;
	}
	
	boolean cracking = false;
	
	// execuï¿½â€¹o da thread
	public void run() {
		try {
			BufferedReader entrada = new BufferedReader
					(new InputStreamReader(conexao.getInputStream()));
			String linha;
			//Send job
			sendMeAJob();
			while (true) {
				// pega o que o servidor enviou
				linha = entrada.readLine();
				System.out.println(linha);
				// verifica se Å½ uma linha vâ€¡lida. Pode ser que a conexâ€¹o
				// foi interrompida. Neste caso, a linha Å½ null. Se isso
				// ocorrer, termina-se a execuï¿½â€¹o saindo com break
				if (linha == null) {
					System.out.println("Conexâ€¹o encerrada!");
					break;
					
				} else {
					validaEntrada(linha);
					//String[] teste = testCrack(0, 100);

				}
				// caso a linha nâ€¹o seja nula, deve-se imprimi-la
				System.out.println();
				System.out.print("> ");
			}
		}
		catch (IOException e) {
			// caso ocorra alguma exceï¿½â€¹o de E/S, mostre qual foi.
			System.out.println("IOException: " + e);
		}
		// sinaliza para o main que a conexâ€¹o encerrou.
		done = true;
	}
	
	private void sendMeAJob() throws IOException {
		if (!cracking) {
			PrintStream saida = new
					PrintStream(conexao.getOutputStream());
			saida.println(MessagesConstants.SEND_JOB+MessagesConstants.SEPARATOR);
		}
	}

	private void validaEntrada(String linha) throws IOException{
		
		String[] message = linha.split(MessagesConstants.SEPARATOR);
		if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.CRACK)) {
			sendMessageBack(message[MessagesConstants.JOB_ID],
					testCrack(message[MessagesConstants.START_INDEX], 
							  message[MessagesConstants.END_INDEX], 
							  message[MessagesConstants.HASH]));
			sendMeAJob();
		} else  if (message[MessagesConstants.COMMAND_CHAR].equals(MessagesConstants.QUEUE)) {
			System.out.println("Job na posição "+message[MessagesConstants.MESSAGE_CHAR]+" da fila");
			sendMeAJob();
		} 
		
	}
	
	private void sendMessageBack(String id, String result) throws IOException {
		
		String msg = MessagesConstants.JOB_RESULT+
				MessagesConstants.SEPARATOR+result+
				MessagesConstants.SEPARATOR+id;
		System.out.println("Enviando Retorno - "+msg);
		PrintStream saida = new PrintStream(conexao.getOutputStream());
		saida.println(msg);
		
	}
	
	public String testCrack(String startIndex, String endIndex, String hash){
		
		int minIndex = Integer.parseInt(startIndex);
		int maxIndex = Integer.parseInt(endIndex);
		
		String[] retorno1 = new String[456976];
		String[] retorno2 = new String[456976];
		BruteIterator generator = new BruteIterator(test1, test2);
		Date t1 = Calendar.getInstance().getTime();
		System.out.println("Cracking "+minIndex+" - "+maxIndex+" at "+Calendar.getInstance().getTime());
		int i = 0;
		while (generator.hasNext())
		{
			retorno1[i] = generator.next();
			i++;
		}	
		retorno2 =retorno1;
		
		//Percorre x indices do array gerado
		System.out.println("Quebrando...");
		for (int j = minIndex; j < maxIndex; j++)
		{
			//System.out.println("checking "+retorno1[j]+"....");
			for (int k = 0; k < retorno2.length; k++)
			{
				//Gera hash md5 e compara com o recebido pela mensagem (no caso foi criado na web e ta hardcoded)
				if (Md5Generator.md5(retorno1[j]+retorno2[k]).equals(hash)){
					return retorno1[j]+retorno2[k]; 
				}
			}
		}
		System.out.println("END PACKAGE!!!");
		return MessagesConstants.NOT_FOUND;
	}
}