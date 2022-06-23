package com.meldakose.homework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "summarize", defaultPhase = LifecyclePhase.INSTALL)
public class SummarizeGoal extends AbstractMojo{
	
	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter
	private String outputFile;

	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub
		
		createFile(this.outputFile);
		String contents ="Project info:"+project.getGroupId()+"."+project.getArtifactId()+"."+project.getVersion();
		
		List<Developer> developers = project.getDevelopers();
		int a=1;
		for (Developer developer : developers) {
			contents=contents+" Developer "+a+" Name:"+developer.getName();
			a++;
		}
		contents=contents+" Release Date:"+project.getProperties().getProperty("release.date");
		List<Dependency> dependencies = project.getDependencies();
		for (Dependency d : dependencies) {
			contents=contents+" Dependency: "+d.getGroupId().toString()+"."+d.getArtifactId().toString();
		}
		
		Set<Artifact> plugins = project.getPluginArtifacts();
		for (Artifact p : plugins) {
			contents=contents+" Plugin: "+p.getArtifactId();
		}
		getLog().info(contents);
		
		writeFile(this.outputFile,contents);

	}
	public void createFile(String path) {
		File file = new File(path);
		try {
			if (file.createNewFile()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeFile(String path,String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
