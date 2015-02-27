package de.rohitmisra.thinWebserver;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.imageio.ImageIO;

public class WebServer {
	
	private int port;
	 
	


	public WebServer(int port){
		setPort(port);
		
	}
	
	public void start() throws Exception{
		
		
		// Establish the listen socket.
		ServerSocket welcomeSocket = new ServerSocket(port);

		// Process HTTP service requests in an infinite loop.
		while (true) {
			// Listen for a TCP connection request.
			Socket connectionSocket = welcomeSocket.accept();

			// Construct an object to process the HTTP request message.
			HttpRequest request = new HttpRequest(connectionSocket);

			// Create a new thread to process the request.
			Thread thread = new Thread(request);

			// Start the thread.
			thread.start();
		}
		
	}
	

	
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}


