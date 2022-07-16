package com.example.bankingsystem;

import java.io.Serializable;

public class Account implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long number;
	private String name;
	private String surname;
	private String email;
	private String tc;
	private String type;
	private double balance;
	private String lastUpdateDate;

	
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}


	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}


	public String toFileFormat() {
		return this.number + "," + this.name + "," + this.surname + "," + this.email + "," + this.tc + "," + this.type+ "," + this.balance+","+this.lastUpdateDate;
	}


	public long getNumber() {
		return number;
	}


	public void setNumber(long number) {
		this.number = number;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSurname() {
		return surname;
	}


	public void setSurname(String surname) {
		this.surname = surname;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getTc() {
		return tc;
	}


	public void setTc(String tc) {
		this.tc = tc;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public double getBalance() {
		return balance;
	}


	public void setBalance(double balance) {
		this.balance = balance;
	}

}
