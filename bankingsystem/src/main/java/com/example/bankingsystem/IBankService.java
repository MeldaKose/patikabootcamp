package com.example.bankingsystem;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

public interface IBankService {
	public ResponseEntity<?> createAccount(Account account);
	public ResponseEntity<?> accountDetail(long number) throws IOException;
	public ResponseEntity<?> deposit(long number,AccountBalanceUpdateRequest request);
	public ResponseEntity<?> transfer(long number,BalanceTransferRequest request);
	public ResponseEntity<?> accountLog(long number) throws IOException;
	public double exchangeAmount(double amount,String toType,String baseType);
}
