package com.example.bankingsystem.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.bankingsystem.exchanger.IAmountExchanger;
import com.example.bankingsystem.models.Account;

public class SerializableAccountRepository implements IAccountRepository {
	
	@Autowired
	private IAmountExchanger amountExchanger;
	
	@Override
	public Account createAccount(String name,String surname,String email,String tc,String type) {
		if(type.equals("USD") || type.equals("TRY") || type.equals("GAU")) {
			Account account=new Account();
			account.setNumber(new Random().nextLong(999999999L,10000000000L));
			account.setId(new Random().nextInt(0,1000000));
			account.setName(name);
			account.setSurname(surname);
			account.setEmail(email);
			account.setTc(tc);
			account.setType(type);
			account.setLastUpdateDate(System.currentTimeMillis());
			account.setBalance(0);
			account.setDeleted(false);
			try {
				ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(new File(""+account.getId())));
				os.writeObject(account);
				os.close();
				return account;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Account accountDetail(int id) throws IOException {
		ObjectInputStream is;
		try {
			is=new ObjectInputStream(new FileInputStream(new File(""+id)));
			Account account =(Account)is.readObject();
			return account;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Account deposit(int id, double balance) {
			try {
				ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(""+id)));
				Account account;
				try {
					account = (Account)is.readObject();
					if(account.getDeleted()==false) {
						account.setBalance(account.getBalance()+balance);
						account.setLastUpdateDate(System.currentTimeMillis());
						ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(new File(""+id)));
						os.writeObject(account);
						os.close();
						return account;
					}else {
						return null;
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		
	}

	@Override
	public String transfer(int id,int transferredAccountId,double amount) {
		ObjectInputStream is;
		ObjectInputStream is2;
		try {
			is=new ObjectInputStream(new FileInputStream(new File(""+id)));
			is2=new ObjectInputStream(new FileInputStream(new File(""+transferredAccountId)));
			try {
				Account transferringAccount=(Account)is.readObject();
				Account transferredAccount=(Account)is2.readObject();
				if(transferringAccount.getDeleted()==false && transferredAccount.getDeleted()==false) {
					double result=amount;
					if(0<=transferringAccount.getBalance()-amount) {
						if(!transferringAccount.getType().equals(transferredAccount.getType())) {
							result=amountExchanger.exchangeAmount(amount, transferredAccount.getType(),transferringAccount.getType());
						}
						transferredAccount.setBalance(transferredAccount.getBalance()+result);
						transferringAccount.setBalance(transferringAccount.getBalance()-amount);
						transferringAccount.setLastUpdateDate(System.currentTimeMillis());
						transferredAccount.setLastUpdateDate(System.currentTimeMillis());
						ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(new File(""+id)));
						os.writeObject(transferringAccount);
						os.close();
						ObjectOutputStream os2=new ObjectOutputStream(new FileOutputStream(new File(""+transferredAccountId)));
						os2.writeObject(transferredAccount);
						os2.close();
						String info=transferringAccount.getNumber()+" transfer amount: "+amount+" "+transferringAccount.getType()+" ,transferred_account: "+transferredAccount.getNumber();
						return info;
					}else {
						return null;
					}
				}else {
					return null;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ArrayList<String> accountLog(int id) throws IOException {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("logs.txt"));
			String line = reader.readLine();
			Account account=this.accountDetail(id);
			ArrayList<String> list = new ArrayList<String>();
			while (line != null) {
				String[] parts=line.split(" ");
				if(parts[0].equals(account.getNumber()+"")) {
					if(parts[1].equals("deposit")) {
						list.add(account.getNumber()+" nolu hesaba "+parts[3]+" "+parts[4]+" yatırılmıştır.");
					}else {
						list.add(account.getNumber()+" hesaptan "+parts[6]+" hesaba "+parts[3]+" "+parts[4]+"  transfer edilmiştir.");
					}
					line = reader.readLine();
				}else {
					line = reader.readLine();
				}
			}  
			
			reader.close();
			return list;
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean deleteAccount(int id) {
		ObjectInputStream is;
		try {
			is=new ObjectInputStream(new FileInputStream(new File(""+id)));
			Account account =(Account)is.readObject();
			if(account.getDeleted() != true) {
				account.setDeleted(true);
				ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(new File(""+id)));
				os.writeObject(account);
				os.close();
				return true;
			}else {
				return false;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void writeAccountLog(String message) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File("logs.txt"),true));
			writer.write(message+"\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

}
