package com.ships;
import java.io.*;
import java.net.*;
import java.security.Timestamp;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import com.bean.CrackJob;

import utils.BruteIterator;
import utils.Md5Generator;

public class Client extends Thread {
	// Flag que indica quando se deve terminar a execuï¿½â€¹o.
	private static boolean done = false;
	//private static char[] inputChars = {'a', 'b', 'c'}; 
	private static char[] test1 = {'a', 'a', 'a','a'};
	private static char[] test2 = {'z', 'z','z','z'};
	
	public static void main(String args[]) {
		//String[] teste = testCrack(0, 100);
		try {
			// Para se conectar a algum servidor, basta se criar um
			// objeto da classe Socket. O primeiro parâ€°metro Å½ o IP ou
			// o endereï¿½o da mâ€¡quina a qual se quer conectar e o
			// segundo parâ€°metro Å½ a porta da aplicaï¿½â€¹o. Neste caso,
			// utiliza-se o IP da mâ€¡quina local (127.0.0.1) e a porta
			// da aplicaï¿½â€¹o ServidorDeChat. Nada impede a mudanï¿½a
			// desses valores, tentando estabelecer uma conexâ€¹o com
			// outras portas em outras mâ€¡quinas.
			Socket conexao = new Socket("127.0.0.1", 2222);
			// uma vez estabelecida a comunicaï¿½â€¹o, deve-se obter os
			// objetos que permitem controlar o fluxo de comunicaï¿½â€¹o
			PrintStream saida = new
					PrintStream(conexao.getOutputStream());
			BufferedReader teclado =
					new BufferedReader(new InputStreamReader(System.in));
			// Uma vez que tudo estâ€¡ pronto, antes de iniciar o loop 
			// principal, executar a thread de recepï¿½â€¹o de mensagens.
			Thread t = new Client(conexao);
			t.start();
			
			// loop principal: obtendo uma linha digitada no teclado e
			// enviando-a para o servidor.
			String linha;
			while (true) {
				// ler a linha digitada no teclado
				System.out.print("> ");
				linha = teclado.readLine();
				// antes de enviar, verifica se a conexâ€¹o nâ€¹o foi fechada
				if (done) {
					break;
				}
				System.out.println("Mensagem Enviada: "+linha);
				// envia para o servidor
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
	// execuï¿½â€¹o da thread
	public void run() {
		try {
			BufferedReader entrada = new BufferedReader
					(new InputStreamReader(conexao.getInputStream()));
			String linha;
			while (true) {
				System.out.println("TreadConnectada");
				// pega o que o servidor enviou
				linha = entrada.readLine();
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
				System.out.println(linha);
				System.out.print("...> ");
			}
		}
		catch (IOException e) {
			// caso ocorra alguma exceï¿½â€¹o de E/S, mostre qual foi.
			System.out.println("IOException: " + e);
		}
		// sinaliza para o main que a conexâ€¹o encerrou.
		done = true;
	}
	
	private static final String CRACK = "C";
	private static final String RESPONSE = "R";
	
	private static final String SEPARATOR = ":";
	private static final int COMMAND_CHAR = 0;
	private static final int START_INDEX = 1;
	private static final int END_INDEX = 2;
	private static final int HASH = 3;
	

	private void validaEntrada(String linha) {
		
		String[] message = linha.split(SEPARATOR);
		System.out.println("Mensagem para crackear: "+linha);
		if (message[COMMAND_CHAR].equals(CRACK)) {
			testCrack(message[START_INDEX], message[END_INDEX], message[HASH]);
			
			System.out.println("CRACK");
		} else  if (message[COMMAND_CHAR].equals(RESPONSE)) {
			
			System.out.println("SEND_JOB");
		}
	}
	
	public String[] testCrack(String startIndex, String endIndex, String hash){
		
		System.out.println("Iniciando o crack");
		int minIndex = Integer.parseInt(startIndex);
		int maxIndex = Integer.parseInt(endIndex);
		
		String[] retorno1 = new String[456976];
		String[] retorno2 = new String[456976];
		BruteIterator generator = new BruteIterator(test1, test2);
		Date t1 = Calendar.getInstance().getTime();
		System.out.println(Calendar.getInstance().getTime());
		int i = 0;
		while (generator.hasNext())
		{
			retorno1[i] = generator.next();
			i++;
		}	
		retorno2 =retorno1;
		Md5Generator md = new Md5Generator();
		
		//Percorre x indices do array gerado
		for (int j = minIndex; j < maxIndex; j++)
		{
			for (int k = 0; k < retorno2.length; k++)
			{
				//Gera hash md5 e compara com o recebido pela mensagem (no caso foi criado na web e ta hardcoded)
//				if (md.md5(retorno1[j]+retorno2[k]).equals("987b43eadf127aaeaf04529c46d23754")){
				if (md.md5(retorno1[j]+retorno2[k]).equals(hash)){
					System.out.println(retorno1[j]+retorno2[k]);
					return retorno1;
				}
			}
		}
		System.out.println(Calendar.getInstance().getTime());
		return retorno1;
	}
}