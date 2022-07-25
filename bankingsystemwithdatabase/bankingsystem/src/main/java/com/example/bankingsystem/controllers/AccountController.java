
package com.example.bankingsystem.controllers;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.example.bankingsystem.models.Account;
import com.example.bankingsystem.repository.IAccountRepository;
import com.example.bankingsystem.requests.AccountBalanceUpdateRequest;
import com.example.bankingsystem.requests.AccountCreateRequest;
import com.example.bankingsystem.requests.BalanceTransferRequest;
import com.example.bankingsystem.responses.AccountCreateSuccessResponse;

@RestController
public class AccountController {
	
	@Autowired
	private KafkaTemplate<String,String> producer;
	
	@Autowired
	@Qualifier("mybatisRepository")
	private IAccountRepository accountRepository;
	
	@PostMapping(path="/accounts")
	public ResponseEntity<?> createAccount(@RequestBody AccountCreateRequest request){
		Account createdAccount=this.accountRepository.createAccount(request.getName(),
				request.getSurname(),
				request.getEmail(),
				request.getTc(),
				request.getType());
		if(createdAccount!=null) {
			AccountCreateSuccessResponse successResponse=new AccountCreateSuccessResponse();
			successResponse.setMessage("Account Created");
			successResponse.setAccountNumber(createdAccount.getNumber());
			return ResponseEntity.ok().body(successResponse);
		}else {
			String message="Invalid Account Type:"+request.getType();
			return ResponseEntity.badRequest().body(message);
		}
	}
	
	@GetMapping(path="/accounts/{id}")
	public ResponseEntity<?> accountDetail(@PathVariable int id,WebRequest request) throws IOException{
		Account detail=this.accountRepository.accountDetail(id);
		if(detail!=null) {
			if(request.checkNotModified(detail.getLastUpdateDate())) {
				return null;
			}
			return ResponseEntity.ok().lastModified(detail.getLastUpdateDate()).body(detail);
			
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PatchMapping(path="/accounts/{id}")
	public ResponseEntity<?> deposit(@PathVariable int id,@RequestBody AccountBalanceUpdateRequest request){
		Account account=this.accountRepository.deposit(id,request.getBalance());
		if(account!=null) {
			String message=account.getNumber()+" deposit amount: "+request.getBalance()+" "+account.getType();
			producer.send("logs", message);
			return ResponseEntity.ok().body(account);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping(path="/accounts/{id}")
	public ResponseEntity<?> transfer(@PathVariable int id,@RequestBody BalanceTransferRequest request){
		String info=this.accountRepository.transfer(id,request.getTransferredAccountId(),request.getAmount());
		if(info!=null) {
			producer.send("logs", info);
			String message="Transferred Successfully";
			return ResponseEntity.ok().body(message);
		}else {
			String message="Insufficient balance";
			return ResponseEntity.badRequest().body(message);
		}
	}
	
	@CrossOrigin(origins={"http://localhost:6162"})
	@GetMapping(path="/accounts/{id}/logs")
	public ResponseEntity<?> accountLog(@PathVariable int id) throws IOException{
		ArrayList<String> logs=this.accountRepository.accountLog(id);
		if(logs!=null) {
			return ResponseEntity.ok().body(logs);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping("/accounts/{id}")
	public ResponseEntity<?> deleteAccount(@PathVariable int id) {
		boolean isDeleted=this.accountRepository.deleteAccount(id);
		if(isDeleted==true) {
			String message="Deleted Successfully";
			return ResponseEntity.ok().body(message);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
}
