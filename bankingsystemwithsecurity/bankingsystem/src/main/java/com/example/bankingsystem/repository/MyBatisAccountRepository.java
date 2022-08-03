package com.example.bankingsystem.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.bankingsystem.exchanger.IAmountExchanger;
import com.example.bankingsystem.models.Account;
import com.example.bankingsystem.models.MyUser;

@Component("mybatisRepository")
public class MyBatisAccountRepository implements IAccountRepository {

	@Autowired
	private IAmountExchanger amountExchanger;

	@Autowired
	private SqlSession session;

	@Override
	public Account createAccount(String name, String surname, String email, String tc, String type) {
		if (type.equals("USD") || type.equals("TRY") || type.equals("GAU")) {
			MyUser authUser = (MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Account account = new Account();
			account.setNumber(new Random().nextLong(999999999L, 10000000000L));
			account.setUser_id(authUser.getId());
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
		}
		return null;
	}

	@Override
	public Account accountDetail(int id) throws IOException {
		Account account = session.selectOne("accountMapper.accountDetail", id);
		if (account != null) {
			MyUser authUser = (MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (account.getUser_id() == authUser.getId()) {
				return account;
			}
		}
		return null;

	}

	@Override
	public Account deposit(int id, double balance) {
		Account account;
		try {
			account = this.accountDetail(id);
			if (account != null) {
				if (account.getDeleted() != true) {
					account.setBalance(account.getBalance() + balance);
					account.setLastUpdateDate(System.currentTimeMillis());
					session.update("deposit", account);
					session.commit();
					return account;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String transfer(int id, int transferredAccountId, double amount) {
		Account transferringAccount;
		try {
			transferringAccount = this.accountDetail(id);
			Account transferredAccount = session.selectOne("accountMapper.accountDetail", transferredAccountId);
			if (transferringAccount != null && transferredAccount != null) {
				if (transferringAccount.getDeleted() == false && transferredAccount.getDeleted() == false) {
					double result = amount;
					if (0 <= transferringAccount.getBalance() - amount) {
						if (!transferringAccount.getType().equals(transferredAccount.getType())) {
							result = amountExchanger.exchangeAmount(amount, transferredAccount.getType(),
									transferringAccount.getType());
						}
						transferredAccount.setBalance(transferredAccount.getBalance() + result);
						transferringAccount.setBalance(transferringAccount.getBalance() - amount);
						transferringAccount.setLastUpdateDate(System.currentTimeMillis());
						transferredAccount.setLastUpdateDate(System.currentTimeMillis());
						session.update("transfer", transferringAccount);
						session.update("transfer", transferredAccount);
						session.commit();
						String info = transferringAccount.getNumber() + " transfer amount: " + amount + " "
								+ transferringAccount.getType() + " ,transferred_account: "
								+ transferredAccount.getNumber();
						return info;
					} else {
						return "Insufficient";
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ArrayList<String> accountLog(int id) throws IOException {
		Account account = session.selectOne("accountMapper.accountDetail", id);
		if (account != null) {
			ArrayList<String> list = new ArrayList<String>();
			List<String> logs = session.selectList("logMapper.accountLog");
			for (String log : logs) {
				String[] parts = log.split(" ");
				if (parts[0].equals(account.getNumber() + "")) {
					if (parts[1].equals("deposit")) {
						list.add(account.getNumber() + " nolu hesaba " + parts[3] + " " + parts[4] + " yatırılmıştır.");
					} else {
						list.add(account.getNumber() + " hesaptan " + parts[6] + " hesaba " + parts[3] + " " + parts[4]
								+ "  transfer edilmiştir.");
					}
				}
			}
			return list;
		}
		return null;

	}

	@Override
	public boolean deleteAccount(int id) {
		Account account = session.selectOne("accountMapper.accountDetail", id);
		if (account != null && account.getDeleted() == false) {
			account.setDeleted(true);
			session.update("accountMapper.deleteAccount", account);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void writeAccountLog(String message) {
		session.insert("logMapper.writeAccountLog", message);
		session.commit();
	}

}
