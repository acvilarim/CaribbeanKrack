package com.bean;

import java.util.Vector;

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
	
	public CrackJob(ClientDetails requestor, String hash) {
		this.hash = hash;
		stevie = new Vector<CrackJob.Job>();
		//Chamar o brute interetor e criar os jobs;
		stevie.add(new Job("AAAA"/*CARACTER*/));
		lastIndexSend = 0;
	}
	
	public Vector<Job> getNextJobs(ClientDetails crackerRequestor) {
		if (status != DONE) {
			if (lastIndexSend+ARGUMENT < stevie.size()) {
				Vector<Job> nexts  = new Vector<Job>();
				for (int i = lastIndexSend; i < lastIndexSend+ARGUMENT; i++) {
					stevie.get(i).setRequestor(crackerRequestor);
					nexts.add(stevie.get(i));
				}
				lastIndexSend += ARGUMENT+1;
				return nexts;
			} else {
				Vector<Job> nexts  = new Vector<Job>();
				for (int i = lastIndexSend; i < stevie.size(); i++) {
					stevie.get(i).setRequestor(crackerRequestor);
					nexts.add(stevie.get(i));
				}
				lastIndexSend += ARGUMENT+1;
				return nexts;
			}
		} else {
			return null;
		}
	}
	
	public void setCrackJobDone(String password) {
		this.status = DONE;
		this.password = password; 
	}
	
	public Vector<Job> getJobs() {
		return stevie;
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
}



