package com.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import utils.BruteIterator;
import utils.Md5Generator;

public class CrackJob implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//private ClientDetails requestor;
	
	private String hash;
	
	public int id;
	
	private int status;
	private Date startAt;
	private Date endAt;
	private static final int WAITING = 0;
	private static final int WORKING = 1;
	private static final int DONE = 2;
	
	private int nextIndexToSend;
	
	public Vector<Job> stevie;
	public Vector<JobIds> deadJobs;
	private final int ARGUMENT = 9;
	
	private String password;
	
	private static char[] test1 = {'a','a','a','a'};
	private static char[] test2 = {'z','z','z','z'};
	
	public CrackJob(ClientDetails requestor, String hash) {
		this.hash = hash;
		status = WAITING;
		//Chamar o brute interetor e criar os jobs;
		stevie = new Vector<CrackJob.Job>();
		deadJobs = new Vector<JobIds>();
		BruteIterator generator = new BruteIterator(test1, test2);
		while (generator.hasNext())
		{
			stevie.add(new Job(generator.next()));	;
		}	
	}
	
	public void setId(int id) {
		if (id == -1) {
			System.out.println("ERRO AO ADICIONAR CRACK JOB");
		} else {
			this.id = id;
		}
	}
	
	public void startJob() {
		startAt = new Date();
		status = WORKING;
		nextIndexToSend = 0;
	}

	
	public int[] getNextJobs(ClientDetails crackerRequestor) {
		int[] ids = new int[2];
		if (deadJobs.size() > 0) {
			JobIds jb = deadJobs.firstElement();
			for (int i = jb.startId; i < jb.endId; i++) {
				stevie.get(i).setRequestor(crackerRequestor);
			}
			deadJobs.remove(jb);
			ids[0] = jb.startId;
			ids[1] = jb.endId;
			return ids;
		}
		if (status == WORKING) {
			ids[0] = nextIndexToSend;
			if (nextIndexToSend+ARGUMENT < stevie.size()) {
				ids[1] = ids[0]+ARGUMENT;
			} else {
				ids[1] = stevie.size();
			}
			nextIndexToSend = ids[1]+1;
			Vector<Job> nexts  = new Vector<Job>();
			for (int i = ids[0]; i <= ids[1]; i++) {
				stevie.get(i).setRequestor(crackerRequestor);
				nexts.add(stevie.get(i));
			}
			return ids;
		}
		
		return null;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setCrackJobDone(String password) {
		this.status = DONE;
		this.password = password; 
		//Chamar o brute interetor e criar os jobs;
		stevie.removeAllElements();
		endAt = new Date();
		System.out.println("Senha Encontrada:" + hash +" = "+password);
		long milis = endAt.getTime() - startAt.getTime();
		System.out.println("Tempo para quebra: "+(milis/1000)+" Segundos");
	}
	
	public Vector<Job> getJobs() {
		return stevie;
	}
	
	public void completeJobsFrom(ClientDetails client) {
		for (Job job : stevie) {
			if ((job.cracker == client) && (job.status == WORKING)) {
				job.setJobDone();
			}
		}
	}
	
	public boolean tryToEndJob(ClientDetails client, String password) {
		if (Md5Generator.md5(password).equals(hash)) {
			this.setCrackJobDone(password);
			return true;
		} else {
			System.out.println("Senha ERRADA!(BUG)");
			return false;
		}
		
	}
	
	public class Job implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String predicado;
		ClientDetails cracker;
		int status;
		
		public Job(String predicado) {
			this.predicado = predicado;
			status = WAITING;
		}
		
		public void setRequestor(ClientDetails cracker) {
			this.cracker = cracker;
			this.status = WORKING;
		}
		
		public void setJobDone() {
			this.status = DONE;
		}
	}
	
	public void printStatus(int jobId) {
		
		System.out.println("------------------------------");
		System.out.println("Job - "+jobId+" - "+hash);
		switch (status) {
		case DONE:
			System.out.println("Status - DONE");
			System.out.println("Password - "+password);
			break;
		case WORKING:
			System.out.println("Status - WORKING");
			int count = 0;
			for (Job job : stevie) {
				if (job.status == DONE) {
					count++;
				}
			}
			float percentil = (100*count)/stevie.size();
			System.out.println(percentil+"% verificada");
			break;
		default:
			System.out.println("Status - WAITING");
			break;
		}
		System.out.println("------------------------------");
	}

	public boolean isProcessing() {
		return status == WORKING;
	}

	public boolean isWaiting() {
		return status == WAITING;
	}

}



