package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class Client manages a connected client. It provides
 * accessor methods to the client and socket.
 * 
 * @author Kathleen
 *
 */
public class Client {
	/**
	 * The socket this client is connected to
	 */
	private Socket socket;
	
	/**
	 * Indicates if the client is connected
	 */
	private boolean connected;
	
	/**
	 * Indicates if the client is valid. An invalid client
	 * has entered invalid data or encountered
	 * an error.
	 */
	private boolean isValid;

	/**
	 * Creates a Client that manages the
	 * parameter socket
	 * @param socket
	 */
	protected Client(Socket socket) {
		this.socket = socket;
		this.connected = true;
		this.isValid = true;
	}

	/**
	 * Indicates if the client is connected
	 * @return true if connected
	 */
	protected boolean isConnected() {
		return connected;
	}
	
	/**
	 * Indicates if the client is valid
	 * @return true if valid
	 */
	protected boolean isValid(){
		return isValid;
	}
	
	/**
	 * Marks the client as invalid.
	 */
	protected void invalidate(){
		isValid = false;
	}

	/**
	 * Provides access to the socket's input
	 * @return BufferedReader - the input stream
	 * @throws IOException
	 */
	protected BufferedReader getInputStream() throws IOException {
		return new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	/**
	 * Provides access to the socket's output
	 * @return PrintWriter - the output stream
	 * @throws IOException
	 */
	protected PrintWriter getOutputStream() throws IOException{
		return new PrintWriter(socket.getOutputStream(), true);
	}

	/**
	 * Disconnects the client and socket
	 * @throws IOException
	 */
	protected void disconnect() throws IOException {
		socket.close();
		connected = false;
		isValid = true;
	}
	
	/**
	 * Forces the client to disconnect
	 */
	protected void forceHalt() throws IOException {
		socket.close();
		connected = false;
		isValid = false;
	}
}
