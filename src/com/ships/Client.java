package com.ships;
import java.io.*;
import java.net.*;
import java.security.Timestamp;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import utils.BruteIterator;

public class Client extends Thread {
	// Flag que indica quando se deve terminar a execuï¿½â€¹o.
	private static boolean done = false;
	//private static char[] inputChars = {'a', 'b', 'c'}; 
	private static char[] test1 = {'a', 'a', 'a','a'};
	private static char[] test2 = {'z', 'z','z','z'};
	
	public static void main(String args[]) {
		String[] teste = generateSequence(test1, test2);
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
			
			//Thread t = new Client(conexao);
			//t.start();
			
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
				System.out.println("Mensagem: "+linha);
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
				// pega o que o servidor enviou
				linha = entrada.readLine();
				// verifica se Å½ uma linha vâ€¡lida. Pode ser que a conexâ€¹o
				// foi interrompida. Neste caso, a linha Å½ null. Se isso
				// ocorrer, termina-se a execuï¿½â€¹o saindo com break
				if (linha == null) {
					System.out.println("Conexâ€¹o encerrada!");
					break;
					
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
	
	private static String[] generateSequence(char[] minIndex, char[] maxIndex){
		String[] retorno1 = new String[456976];
		String[] retorno2 = new String[456976];
		int index = 0;
		BruteIterator generator = new BruteIterator(minIndex, maxIndex);
		BruteIterator generator2 = new BruteIterator(minIndex, maxIndex);
		Date t1 = Calendar.getInstance().getTime();
		System.out.println(Calendar.getInstance().getTime());
		String palavra = "";
		int i = 0;
		while (generator.hasNext())
		{
			retorno1[i] = generator.next();
			i++;
		}	
		int l = 0;
		while (generator2.hasNext())
		{
			retorno2[l] = generator2.next();
			l++;
		}
		
		for (int j = 2; j > 0; j--)
		{
			for (int k = 0; k < retorno2.length; k++)
			{
				System.out.println(retorno1[j]+retorno2[k]);
			}
		}
		System.out.println(t1);
		System.out.println(Calendar.getInstance().getTime());
		return retorno1;
	}
}