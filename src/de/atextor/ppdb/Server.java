package de.atextor.ppdb;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

public class Server {
	private SelectorThread threadSelector = null;
	public static File sqliteFile = null;

	public Server() throws IllegalArgumentException, IOException {
		final String baseUri = "http://localhost:9998/";
		final Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("com.sun.jersey.config.property.packages", "de.atextor.ppdb");
		Page.init();
		threadSelector = GrizzlyWebContainerFactory.create(baseUri, initParams);
	}

	public void stop() {
		if (this.threadSelector != null) {
			this.threadSelector.stopEndpoint();
		}
	}
	
	public static void main(String[] args) throws IOException {
		final File[] sqliteFiles = new File(".").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".sqlite");
			}
		});
		
		if (sqliteFiles.length != 1) {
			System.out.println("Not exactly one sqlite file found in directory.");
			System.exit(1);
		}
		
		sqliteFile = sqliteFiles[0];
		
		Server s = new Server();
		System.in.read();
		s.stop();
	}
}
