package com.example.movieproject;


import java.util.ArrayList;
import java.util.List;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;


@Component
public class MovieService implements IMovieService {
	
	//Method that pulls movie information with name parameter
	@Override
	public List<Movie> searchByName(String movieName) {
		RestTemplate client=new RestTemplate();
		HttpHeaders headers=new HttpHeaders();
		headers.add("content-type", "application/json");
		headers.add("authorization", "apikey 7sHFLxhU6cFxmwpYGQlZLs:45NkMwNSKgg5ha8N1131Qj");
		String url="https://api.collectapi.com/imdb/imdbSearchByName?query=" + movieName;
		HttpEntity<?> requestEntity=new HttpEntity<>(headers);
		ResponseEntity<String> response=client.exchange(url, HttpMethod.GET,requestEntity,String.class);
		String res=response.getBody();
		ObjectMapper objectMapper=new ObjectMapper();
		List<Movie> movies=new ArrayList<Movie>();
		try {
			JsonNode node=objectMapper.readTree(res);
			JsonNode resultNode=node.get("result");
			if(resultNode.isArray()) {
				ArrayNode moviesNode=(ArrayNode) resultNode;
				for(int i=0;i<moviesNode.size();i++) {
					JsonNode singleMovie=moviesNode.get(i);
					String title=singleMovie.get("Title").toString();
					String year=singleMovie.get("Year").toString();
					String imdbId=singleMovie.get("imdbID").toString();
					String type=singleMovie.get("Type").toString();
					Movie m=new Movie();
					m.setImdbID(imdbId);
					m.setTitle(title);
					m.setType(type);
					m.setYear(year);
					movies.add(m);
				
				}
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return movies;
	}
	
	
	//Method that pulls movie detail with id parameter
	@Override
	public Movie searchById(String id) {
		RestTemplate client=new RestTemplate();
		HttpHeaders headers=new HttpHeaders();
		headers.add("content-type", "application/json");
		headers.add("authorization", "apikey 7sHFLxhU6cFxmwpYGQlZLs:45NkMwNSKgg5ha8N1131Qj");
		String url="https://api.collectapi.com/imdb/imdbSearchById?movieId=" + id;
		HttpEntity<?> requestEntity=new HttpEntity<>(headers);
		ResponseEntity<String> response=client.exchange(url, HttpMethod.GET,requestEntity,String.class);
		String res=response.getBody();
		ObjectMapper objectMapper=new ObjectMapper();
		Movie m=new Movie();
		try {
			JsonNode node=objectMapper.readTree(res);
			JsonNode resultNode=node.get("result");
			String title=resultNode.get("Title").toString();
			String year=resultNode.get("Year").toString();
			String imdbId=resultNode.get("imdbID").toString();
			String type=resultNode.get("Type").toString();
			m.setImdbID(imdbId);
			m.setTitle(title);
			m.setType(type);
			m.setYear(year);
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	

	

}
