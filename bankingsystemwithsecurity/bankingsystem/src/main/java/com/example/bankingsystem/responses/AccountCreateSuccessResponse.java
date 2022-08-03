package com.example.bankingsystem.responses;


public class AccountCreateSuccessResponse {
	private String message;
	private long accountNumber;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}
	
}
