package com.example.bankingsystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankingsystem.repository.MyBatisUserRepository;
import com.example.bankingsystem.requests.LoginRequest;
import com.example.bankingsystem.responses.LoginSuccessResponse;
import com.example.bankingsystem.security.JWTTokenUtil;

@RestController
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JWTTokenUtil jwtTokenUtil;
	@Autowired
	private MyBatisUserRepository myBatisUserRepository;
	
	@PostMapping("/auth")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		} catch (BadCredentialsException e) {
			return ResponseEntity.badRequest().body("Bad credentials");
		} catch (DisabledException e) {
		}
		final UserDetails userDetails = myBatisUserRepository
				.loadUserByUsername(request.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		LoginSuccessResponse response = new LoginSuccessResponse();
		response.setStatus("success");
		response.setToken(token);
        return ResponseEntity.ok().body(response);
	}
}
