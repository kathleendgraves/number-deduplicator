package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClientHandler creates a thread to manage a 
 * socket connection. It determines client connection
 * and manages client input by calling the managed
 * client.
 * 
 * @author Kathleen
 *
 */
public class ClientHandler extends Thread {
	/**
	 * The client for this handler
	 */
	private Client client;
	
	/**
	 * Indicates if the handler is running
	 */
	private volatile boolean running = true;
	
	/**
	 * The input command that will exit the client
	 * and end the program.
	 */
	private static String exitCommand = "terminate";
	
	protected ClientHandler(Client client) {
		this.client = client;
	}
	
	/**
	 * Indicates if the client is connected
	 * 
	 * @return true if the client is connected
	 */
	protected boolean clientIsConnected(){
		return client.isConnected();
	}
	
	/**
	 * Indicates if the client is valid. 
	 * 
	 * Note: Invalid clients have entered invalid input and
	 * will be terminated.
	 * 
	 * @return true if the client is invalid
	 */
	protected boolean clientIsValid(){
		return client.isValid();
	}
	
	/**
	 * Disconnects the client and terminates the handler
	 * @throws IOException
	 */
	protected void terminate() throws IOException {
        this.running = false;
        client.disconnect();
    }
	
	/**
	 * Marks the client as invalid
	 */
	private void invalidateClient(){
		client.invalidate();
	}
	
	@Override
	public void run() {

		try (
				// Open input and streams
				BufferedReader input = client.getInputStream();
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			) {
			String userInput;
			// Precompile pattern for better performance
			Pattern inputPattern = Pattern.compile("\\d{9}");
			Matcher matcher = inputPattern.matcher("");
			
			// Retrieve unique input list singleton
			HashSet<String> inputSet = UniqueInputHash.getInstance();
			
			while (running) {
				if ((userInput = input.readLine()) != null) {
		            
					// Check that input is valid
					if (matcher.reset(userInput).matches()) {

						// Thread-safe insert
						synchronized (inputSet) {
							if (inputSet.add(userInput)) {
								UniqueInputHash.incrementUniques();
							} else {
								UniqueInputHash.incrementDuplicates();
							}
						}
						
					} else if (userInput.equals(exitCommand)){
						this.running = false;
					} else {
						// Invalid input - stop running
						this.running = false;
						out.println("terminate");
						invalidateClient();
					}
				}
				
			} // end while
			
			input.close();
			
			if (client.isValid()){
				// Received quit command
				client.disconnect();
			} else {
				// Error or invalid input
				client.forceHalt();
			}
			
		} catch (IOException e) {				
			System.err.println("Encountered error from client: " + e.getMessage());
			try {
				client.forceHalt();
			} catch (IOException e1) {
				System.err.println("Error disconnecting client: " + e.getMessage());
			}
		} 
	}

}