package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;

/**
* The DeduplicationServer program opens a socket that accepts nine digit numbers from
* up to 5 concurrent clients. The numbers are de-duplicated and stored in
* a text file upon completion of the program. Any invalid input (eg: strings,
* numbers that are not nine digits) closes the client connection. Any client
* that enters "terminate" will end the entire program.
*
* @author Kathleen Graves
* @version 1.0
* @since 1/29/17 
*/
public class DeduplicationServer {
	// Always use port 4000
	private static int portNumber = 4000;

	public static void main(String[] args) throws Exception {
		boolean running = true;

		// Create server socket
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.err.println("Error creating server socket: " + e.getMessage());
			System.exit(1);
		}

		// Create main handler
		SocketHandler mainHandler = new SocketHandler(new DeduplicationSocket(serverSocket));
		mainHandler.start();

		// Check that all clients are connected
		while (running) {

			// If one of the clients disconnected, stop running
			if (!mainHandler.allValidClientsConnected()) {
				running = false;
			}

		}

		// Disconnect main socket
		mainHandler.terminate();

		// Put contents of hashset in file
		LogFile log = new LogFile();

		Iterator<String> setIterator = mainHandler.getInputIterator();
		while (setIterator.hasNext()) {
			log.writeToLog(setIterator.next());
		}
		log.close();

		// Exit
		serverSocket.close();
		System.exit(1);

	}
}
