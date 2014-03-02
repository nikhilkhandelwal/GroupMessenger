package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;

public class Message implements Serializable, Comparable<Message> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int mask= -1;
	public Message() {
		super();
		this.serialNumber = mask;
	}
	public int getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	private int serialNumber;
	private String message;
	@Override
	public int compareTo(Message another) {
		// TODO Auto-generated method stub
		return this.serialNumber - another.serialNumber ;
	}

}
