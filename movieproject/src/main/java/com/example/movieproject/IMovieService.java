package com.example.movieproject;

import java.util.List;

public interface IMovieService {
	
	public List<Movie> searchByName(String movieName);
	public Movie searchById(String id);
	
}
