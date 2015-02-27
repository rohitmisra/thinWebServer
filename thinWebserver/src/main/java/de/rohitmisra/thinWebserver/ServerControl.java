package de.rohitmisra.thinWebserver;

public class ServerControl {

	
	
	public static void main(String [] args){
		
		try {
			WebServer webServer = new WebServer(9997);
			webServer.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
}
