package com.example.movieproject;

import java.util.List;

public class MovieResponse {
	private boolean success;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<Movie> getResult() {
		return result;
	}
	public void setResult(List<Movie> result) {
		this.result = result;
	}
	private List<Movie> result;
}
