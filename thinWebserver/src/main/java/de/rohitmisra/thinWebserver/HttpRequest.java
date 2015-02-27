package de.rohitmisra.thinWebserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

final class HttpRequest implements Runnable {
	final static String CRLF = "\r\n";// returning carriage return (CR) and a
	// line feed (LF)
	Socket socket;

	// Constructor
	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
	// Within run(), we explicitly catch and handle exceptions with a try/catch
	// block.
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {
		// Get a reference to the socket's input and output streams.
		InputStream instream = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		// Set up input stream filters.
		// Page 169 10th line down or so...
		BufferedReader br = new BufferedReader(new InputStreamReader(instream));// reads the input data

		// Get the request line of the HTTP request message.
		String requestLine = br.readLine();// get /path/file.html version of
		// http

		// Display the request line.
		System.out.println();
		System.out.println(requestLine);
		// HERE WE NEED TO DEAL WITH THE REQUEST
		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which should be "GET"
		String fileName = tokens.nextToken();

		// Prepend a "." so that file request is within the current directory.
		fileName = "." + "\\WebContent\\" + fileName;
		System.out.println(System.getProperty("user.dir"));
		// Open the requested file.

		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;

		if (fileExists) {
			statusLine = "HTTP/1.0 200 OK" + CRLF; // common success message
			contentTypeLine = "Content-type: " + contentType(fileName) + "\r\n";
		}// content info

		else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;// common error message
			contentTypeLine = "Content-type: " + "text/html" + "\r\n\r\n";// content
			// info
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>"
					+ "<BODY>Not Found</BODY></HTML>";
		}

		// Send the status line.
		os.writeBytes(statusLine);

		// Send the content type line.
		os.writeBytes(contentTypeLine);

		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);

		// Send the entity body.
		if (fileExists) {
			sendBytes(fis, os, fileName);
			//os.writeBytes(statusLine);//Send the status line.
			// os.writeBytes(contentTypeLine);//Send the content type line.
			fis.close();
		} else {
			System.out.println("*File not found***");
			os.writeBytes(statusLine);// Send the status line.
			os.writeBytes(entityBody);// Send the an html error message info
			// body.
			os.writeBytes(contentTypeLine);// Send the content type line.
		}

		System.out.println("*****");
		System.out.println(fileName);// print out file request to console
		System.out.println("*****");
		// Get and display the header lines.
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}

	
		// Close streams and socket.
		os.close();
		br.close();
		socket.close();

	}

	// return the file types
	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		if (fileName.endsWith(".png")) {
			return "image/png";
		}
		if (fileName.endsWith(".gif")) {
			return "image/gif";
		}
		if (fileName.endsWith(".css")) {
			return "text/css";
		}
		return "application/octet-stream";
	}

	// set up input output streams
	private static void sendBytes(FileInputStream fis, OutputStream os, String fileName)
			throws Exception {
	
			// Construct a 1K buffer to hold bytes on their way to the socket.
			byte[] buffer = new byte[1024];
			int bytes = 0;

			// Copy requested file into the socket's output stream.
			while ((bytes = fis.read(buffer)) != -1)// read() returns minus one,
				// indicating that the end of
				// the file
			{
				os.write(buffer, 0, bytes);
			}
	}
}