package com.example.bankingsystem.requests;

public class BalanceTransferRequest {
	private int transferredAccountId;
	private double amount;
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getTransferredAccountId() {
		return transferredAccountId;
	}
	public void setTransferredAccountId(int transferredAccountId) {
		this.transferredAccountId = transferredAccountId;
	}
	
	
}
