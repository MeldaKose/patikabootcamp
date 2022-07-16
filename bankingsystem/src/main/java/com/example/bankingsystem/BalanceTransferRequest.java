package com.example.bankingsystem;

public class BalanceTransferRequest {
	private long transferredAccountNumber;
	private double amount;
	public long getTransferredAccountNumber() {
		return transferredAccountNumber;
	}
	public void setTransferredAccountNumber(long transferredAccountNumber) {
		this.transferredAccountNumber = transferredAccountNumber;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
}
