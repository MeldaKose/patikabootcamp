package com.example.bankingsystem.repository;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bankingsystem.exchanger.IAmountExchanger;
import com.example.bankingsystem.models.Account;

@Component("mybatisRepository")
public class MyBatisRepository implements IAccountRepository{
	
	@Autowired
	private IAmountExchanger amountExchanger;
	
	private String configFile="mapper/myBatis_conf.xml";
	
	@Override
	public Account createAccount(String name, String surname, String email, String tc, String type) {
		if(type.equals("USD") || type.equals("TRY") || type.equals("GAU")) {
			Reader reader;
			try {
				reader = Resources.getResourceAsReader(configFile);
				SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
				SqlSession session=sqlSessionFactory.openSession();
				Account account =new Account();
				account.setNumber(new Random().nextLong(999999999L,10000000000L));
				account.setName(name);
				account.setSurname(surname);
				account.setEmail(email);
				account.setTc(tc);
				account.setType(type);
				account.setBalance(0);
				account.setLastUpdateDate(System.currentTimeMillis());
				session.insert("accountMapper.createAccount", account);
				session.commit();
				return account;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
	}

	@Override
	public Account accountDetail(int id) throws IOException {
		Reader reader=Resources.getResourceAsReader(configFile);
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
		SqlSession session=sqlSessionFactory.openSession();
		Account account=session.selectOne("accountMapper.accountDetail", id);
		if(account != null) {
			return account;
		}else {
			return null;
		}
	}

	@Override
	public Account deposit(int id, double balance) {
		try {
			Reader reader=Resources.getResourceAsReader(configFile);
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
			SqlSession session=sqlSessionFactory.openSession();
			Account account =this.accountDetail(id);
			if(account.getDeleted()==false) {
				account.setBalance(account.getBalance()+balance);
				account.setLastUpdateDate(System.currentTimeMillis());
				session.update("deposit", account);
				session.commit();
				return account;
			}else {
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public String transfer(int id, int transferredAccountId, double amount) {
		Reader reader;
		try {
			reader = Resources.getResourceAsReader(configFile);
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
			SqlSession session=sqlSessionFactory.openSession();
			try {
				Account transferringAccount =this.accountDetail(id);
				Account transferredAccount =this.accountDetail(transferredAccountId);
				if(transferringAccount.getDeleted() == false && transferredAccount.getDeleted() == false) {
					double result=amount;
					if(0<=transferringAccount.getBalance()-amount) {
						if(!transferringAccount.getType().equals(transferredAccount.getType())) {
							result=amountExchanger.exchangeAmount(amount, transferredAccount.getType(),transferringAccount.getType());
						}
						transferredAccount.setBalance(transferredAccount.getBalance()+result);
						transferringAccount.setBalance(transferringAccount.getBalance()-amount);
						transferringAccount.setLastUpdateDate(System.currentTimeMillis());
						transferredAccount.setLastUpdateDate(System.currentTimeMillis());
						session.update("transfer", transferringAccount);
						session.update("transfer", transferredAccount);
						session.commit();
						String info=transferringAccount.getNumber()+" transfer amount: "+amount+" "+transferringAccount.getType()+" ,transferred_account: "+transferredAccount.getNumber();
						return info;
					}
				}else {
					return null;
				}
			} catch (IOException e) {
				session.rollback();
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public ArrayList<String> accountLog(int id) throws IOException {
		Reader reader= Resources.getResourceAsReader(configFile);
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
		SqlSession session=sqlSessionFactory.openSession();
		Account account =this.accountDetail(id);
		ArrayList<String> list = new ArrayList<String>();
		List<String> logs=session.selectList("accountMapper.accountLog");
		for(String log : logs) {
			String[] parts=log.split(" ");
			if(parts[0].equals(account.getNumber()+"")) {
				if(parts[1].equals("deposit")) {
					list.add(account.getNumber()+" nolu hesaba "+parts[3]+" "+parts[4]+" yatırılmıştır.");
				}else {
					list.add(account.getNumber()+" hesaptan "+parts[6]+" hesaba "+parts[3]+" "+parts[4]+"  transfer edilmiştir.");
				}
			}
		}
		return list;
	}

	@Override
	public boolean deleteAccount(int id) {
		Reader reader;
		try {
			reader = Resources.getResourceAsReader(configFile);
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
			SqlSession session=sqlSessionFactory.openSession();
			Account account =this.accountDetail(id);
			if(account.getDeleted() == false) {
				account.setDeleted(true);
				session.update("deleteAccount",account);
				return true;
			}else {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void writeAccountLog(String message) {
		Reader reader;
		try {
			reader = Resources.getResourceAsReader(configFile);
			SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
			SqlSession session=sqlSessionFactory.openSession();
			session.insert("writeAccountLog",message);
			session.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
