package com.example.bankingsystem.responses;

public class LoginSuccessResponse {
	private String status;
	private String token;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
