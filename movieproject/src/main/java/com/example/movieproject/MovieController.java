package com.example.movieproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {
	
	@Autowired
	private IMovieService movieService;
	
	@RequestMapping(path="/movies/search",method=RequestMethod.GET)
	public List<Movie> search(@RequestParam(name="movie_name") String name) {
		return this.movieService.searchByName(name);
	}
	
	//Method that saves the value found according to the id to the file
	@RequestMapping(path="/movies/saveToList",method=RequestMethod.GET)
	public boolean addToList(@RequestParam(name="id") String id) {
		Movie m= this.movieService.searchById(id);
		String fileLine =m.getImdbID()+ ","+m.getTitle()+","+m.getType()+","+m.getYear();
		try {
		BufferedWriter writer=new BufferedWriter(new FileWriter(new File("movies.txt")));
		writer.write(fileLine);
		writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	//method that reads the data sought from the file
	@RequestMapping(path="/movies/detail",method=RequestMethod.GET)
	public Movie findById(@RequestParam(name="id") String id) throws IOException{
		
		String newId="\""+id+"\"";
		try {
			BufferedReader reader=new BufferedReader(new FileReader("movies.txt"));
			String line = reader.readLine();
			while (line != null) {
				String[] parts=line.split(",");
				if(parts[0].equals(newId)) {
					Movie m =new Movie();
					m.setImdbID(parts[0]);
					m.setTitle(parts[1]);
					m.setType(parts[2]);
					m.setYear(parts[3]);
					reader.close();
					return m;
				}else {
					line = reader.readLine();
				}
				
			}     
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.movieService.searchById(id);
	}
	
}
