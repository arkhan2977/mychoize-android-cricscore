package com.mychoize.android.cricscore.app;

public class SMSContact {
	
	private int id;
	private String name;
	private String number;
	private boolean connected;
	
	public SMSContact(String name, String number) {
		super();
		this.name = name;
		this.number = number;
		this.connected = true;
	}
	
	
	
	public SMSContact(int id, String name, String number, int connected) {
		super();
		this.id = id;
		this.name = name;
		this.number = number;
		setConnected(connected);
	}



	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public void setConnected(int connected){
		if(connected == 0){
			this.connected = false;
		}
		else{
			this.connected = true;
		}
	}
	
	public int getConnected(){
		if(this.connected){
			return 1;
		}
		return 0;
	}
	
	
}
