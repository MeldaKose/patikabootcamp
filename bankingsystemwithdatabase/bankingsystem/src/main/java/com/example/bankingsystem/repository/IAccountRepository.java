package com.example.bankingsystem.repository;

import java.io.IOException;
import java.util.ArrayList;


import com.example.bankingsystem.models.Account;

public interface IAccountRepository {
	public Account createAccount(String name,String surname,String email,String tc,String type);
	public Account accountDetail(int id) throws IOException;
	public Account deposit(int id,double balance);
	public String transfer(int id,int transferredAccountId,double amount) ;
	public ArrayList<String> accountLog(int id) throws IOException;
	public void writeAccountLog(String message);
	public boolean deleteAccount(int id);
}
