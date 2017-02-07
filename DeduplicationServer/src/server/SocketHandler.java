package server;

import java.io.IOException;
import java.net.SocketException;
import java.util.Timer;

/**
 * Class SocketHandler creates a thread for managing
 * a server socket connection. It creates handlers for new 
 * connections and removes invalid clients.
 * 
 * @author Kathleen
 *
 */
public class SocketHandler extends Thread {
	private DeduplicationSocket numberSocket = null;
	private volatile boolean running = true;
	private Timer reportTimer;
	
	protected SocketHandler(DeduplicationSocket socket) {
		this.numberSocket = socket;
		this.reportTimer = new Timer();
	}
	
	/**
	 * Checks that all valid clients are still connected.
	 * 
	 * @return True if all clients connected
	 */
	protected boolean allValidClientsConnected(){
		return numberSocket.allClientsConnected();
	}
	
	/**
	 * Returns an iterator for all unique client input
	 * 
	 * @return Iterator<String>
	 */
	protected java.util.Iterator<String> getInputIterator(){
		return numberSocket.getInputIterator();
	}
	
	/**
	 * Disconnects the server socket and any children
	 * 
	 * @throws IOException
	 */
	protected void terminate() throws IOException {
        this.running = false;
        reportTimer.cancel();
        numberSocket.disconnect();
    }
	
	@Override
	public void run() {
		
		// Schedule report
		ReportTask runningReport = new ReportTask();
		reportTimer.scheduleAtFixedRate(runningReport, 10000, 10000);

		while (running) {
			// Clean up invalid connections
			numberSocket.removeInvalidClients();
			
			try {
				// Limit to five connections
				if (numberSocket.numberOfConnections() < 5) {
					numberSocket.addNewClient();
				}
				
			} catch (SocketException e){
				if (!(e.getMessage().equals("socket closed") && running == false)){
					System.err.println("Error creating sockets: " + e.getMessage());
					running = false;	
				}
			} catch (IOException e) {
				System.err.println("Unexpected error: " + e.getMessage());
				running = false;
			} 
		}
	}
	
}
