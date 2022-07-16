package com.example.bankingsystem;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankController {
	
	@Autowired
	private IBankService bankService;
	
	@PostMapping(path="/accounts")
	public ResponseEntity<?> createAccount(@RequestBody Account request){
		return this.bankService.createAccount(request);
	}
	
	@GetMapping(path="/accounts/{number}")
	public ResponseEntity<?> accountDetail(@PathVariable long number) throws IOException{
		return this.bankService.accountDetail(number);
	}
	
	@PatchMapping(path="/accounts/{number}")
	public ResponseEntity<?> deposit(@PathVariable long number,@RequestBody AccountBalanceUpdateRequest request){
		return this.bankService.deposit(number,request);
	}
	
	@PostMapping(path="/accounts/{number}")
	public ResponseEntity<?> transfer(@PathVariable long number,@RequestBody BalanceTransferRequest request){
		return this.bankService.transfer(number,request);
	}
	
	@CrossOrigin(origins={"http://localhost"})
	@GetMapping(path="/accounts/{number}/logs")
	public ResponseEntity<?> accountLog(@PathVariable long number) throws IOException{
		return this.bankService.accountLog(number);
	}
	
}
