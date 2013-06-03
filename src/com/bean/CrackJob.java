package com.bean;

import java.util.Vector;

import utils.BruteIterator;
import utils.Md5Generator;

public class CrackJob {
	
	private ClientDetails requestor;
	
	private String hash;
	
	private int status;
	private static final int WAITING = 0;
	private static final int WORKING = 1;
	private static final int DONE = 2;
	
	private int lastIndexSend;
	
	private Vector<Job> stevie;
	private final int ARGUMENT = 100;
	
	private String password;
	
	private static char[] test1 = {'a', 'a', 'a','a'};
	private static char[] test2 = {'z', 'z','z','z'};
	
	public CrackJob(ClientDetails requestor, String hash) {
		this.hash = hash;
		stevie = new Vector<CrackJob.Job>();
		//Chamar o brute interetor e criar os jobs;
		BruteIterator generator = new BruteIterator(test1, test2);
		while (generator.hasNext())
		{
			stevie.add(new Job(generator.next()));	;
		}	
	}
	
	public void startJob() {
		status = WORKING;
		lastIndexSend = 0;
	}

	
	public int[] getNextJobs(ClientDetails crackerRequestor) {
		if (status == WORKING) {
			int[] ids = new int[2];
			if (lastIndexSend+1+ARGUMENT < stevie.size()) {
				Vector<Job> nexts  = new Vector<Job>();
				for (int i = lastIndexSend; i < lastIndexSend+ARGUMENT; i++) {
					stevie.get(i).setRequestor(crackerRequestor);
					nexts.add(stevie.get(i));
				}
				ids[0] = lastIndexSend+1;
				ids[1] = ids[0]+ARGUMENT;
				lastIndexSend += ARGUMENT;
			} else {
				Vector<Job> nexts  = new Vector<Job>();
				for (int i = lastIndexSend; i < stevie.size(); i++) {
					stevie.get(i).setRequestor(crackerRequestor);
					nexts.add(stevie.get(i));
				}
				ids[0] = lastIndexSend+1;
				ids[1] = stevie.size();
			}
			return ids;
		} else {
			return null;
		}
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setCrackJobDone(String password) {
		this.status = DONE;
		this.password = password; 
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
		if (Md5Generator.md5(password) == hash) {
			this.setCrackJobDone(password);
			return true;
		} else {
			return false;
		}
		
	}
	private class Job {
		String predicado;
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

}



