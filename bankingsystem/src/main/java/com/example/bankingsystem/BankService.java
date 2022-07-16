package com.example.bankingsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
public class BankService implements IBankService {
	
	@Autowired
	private KafkaTemplate<String,String> producer;
	
	@Autowired
	private RestTemplate client;
	
	@Override
	public ResponseEntity<?> createAccount(Account request) {
		if(request.getType().equals("USD") || request.getType().equals("TRY") || request.getType().equals("GAU")) {
			Account account=new Account();
			account.setNumber(new Random().nextLong(999999999L,10000000000L));
			account.setName(request.getName());
			account.setSurname(request.getSurname());
			account.setEmail(request.getEmail());
			account.setTc(request.getTc());
			account.setType(request.getType());
			//account.setLastUpdateDate(new Date(System.currentTimeMillis()));
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date(System.currentTimeMillis());
			account.setLastUpdateDate(dateFormat.format(date));
			account.setBalance(0);
			try {
				ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(new File(""+account.getNumber())));
				os.writeObject(account);
				os.close();
				AccountCreateSuccessResponse successResponse=new AccountCreateSuccessResponse();
				successResponse.setMessage("Account Created");
				successResponse.setAccountNumber(account.getNumber());
				return ResponseEntity.ok().body(successResponse);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			String message="Invalid Account Type:"+request.getType();
			return ResponseEntity.badRequest().body(message);
		}
		return null;
	}

	@Override
	public ResponseEntity<?> accountDetail(long number) throws IOException {
		ObjectInputStream is=new ObjectInputStream(new FileInputStream(new File(""+number)));
		try {
			Account account =(Account)is.readObject();
			return ResponseEntity.ok().body(account);
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
	public ResponseEntity<?> deposit(long number, AccountBalanceUpdateRequest request) {
		try {
			ObjectInputStream is=new ObjectInputStream(new FileInputStream(new File(""+number)));
			try {
				Account account=(Account)is.readObject();
				account.setBalance(account.getBalance()+request.getBalance());
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date(System.currentTimeMillis());
				account.setLastUpdateDate(dateFormat.format(date));
				ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(new File(""+account.getNumber())));
				os.writeObject(account);
				os.close();
				String message=number+" deposit amount: "+request.getBalance()+" "+account.getType();
				producer.send("logs", message);
				return ResponseEntity.ok().body(account);
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
	public ResponseEntity<?> transfer(long number, BalanceTransferRequest request) {
		try {
			ObjectInputStream is=new ObjectInputStream(new FileInputStream(new File(""+number)));
			ObjectInputStream is2=new ObjectInputStream(new FileInputStream(new File(""+request.getTransferredAccountNumber())));
			try {
				Account transferringAccount=(Account)is.readObject();
				Account transferredAccount=(Account)is2.readObject();
				if(0<=transferringAccount.getBalance()-request.getAmount()) {
					if(transferringAccount.getType()!=transferredAccount.getType()) {
						double result=this.exchangeAmount(request.getAmount(), transferredAccount.getType(),transferringAccount.getType());
						transferredAccount.setBalance(transferredAccount.getBalance()+result);
					}else {
						transferredAccount.setBalance(transferredAccount.getBalance()+request.getAmount());
					}
					transferringAccount.setBalance(transferringAccount.getBalance()-request.getAmount());
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date(System.currentTimeMillis());
					transferringAccount.setLastUpdateDate(dateFormat.format(date));
					transferredAccount.setLastUpdateDate(dateFormat.format(date));
					ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(new File(""+number)));
					os.writeObject(transferringAccount);
					os.close();
					ObjectOutputStream os2=new ObjectOutputStream(new FileOutputStream(new File(""+request.getTransferredAccountNumber())));
					os2.writeObject(transferredAccount);
					os2.close();
					String logMessage=number+" transfer amount: "+request.getAmount()+" "+transferringAccount.getType()+" ,transferred_account: "+request.getTransferredAccountNumber();
					producer.send("logs", logMessage);
					String message="Transferred Successfully";
					return ResponseEntity.ok().body(message);
				}else {
					String message="Insufficient balance";
					return ResponseEntity.badRequest().body(message);
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
	public ResponseEntity<?> accountLog(long number) throws IOException {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("logs.txt"));
			String line = reader.readLine();
			ArrayList<String> list = new ArrayList<String>();
			while (line != null) {
				String[] parts=line.split(" ");
				if(parts[0].equals(number+"")) {
					if(parts[1].equals("deposit")) {
						list.add(number+" nolu hesaba "+parts[3]+" "+parts[4]+" yatırılmıştır.");
					}else {
						list.add(number+" hesaptan "+parts[6]+" hesaba "+parts[3]+" "+parts[4]+"  transfer edilmiştir.");
					}
					line = reader.readLine();
				}else {
					line = reader.readLine();
				}
			}  
			
			reader.close();
			return ResponseEntity.ok().body(list);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public double exchangeAmount(double amount,String toType,String baseType) {
		HttpHeaders headers=new HttpHeaders();
		headers.add("content-type", "application/json");
		headers.add("authorization", "apikey 7sHFLxhU6cFxmwpYGQlZLs:45NkMwNSKgg5ha8N1131Qj");
		String url="https://api.collectapi.com/economy/exchange?int="+amount+"&to="+toType+"&base="+baseType;
		HttpEntity<?> requestEntity=new HttpEntity<>(headers);
		ResponseEntity<String> response=client.exchange(url, HttpMethod.GET,requestEntity,String.class);
		String res=response.getBody();
		ObjectMapper objectMapper=new ObjectMapper();
		Double result=0.0;
		JsonNode node;
		try {
			node = objectMapper.readTree(res);
			JsonNode resultNode=node.get("result");
			ArrayNode data=(ArrayNode) resultNode.get("data");
			JsonNode singledata=data.get(0);
			result=singledata.get("calculated").asDouble();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	

}
