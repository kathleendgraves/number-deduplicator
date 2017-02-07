package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Class DeduplicationSocket manages a ServerSocket. It
 * keeps a list of unique inputs entered by clients
 * and a list of handlers for attached clients.
 * 
 * @author Kathleen
 *
 */
public class DeduplicationSocket{
	/**
	 *  The ServerSocket
	 */
	private ServerSocket socket;
	
	/**
	 *  Unique hash set of inputs entered by all clients
	 */
	private HashSet<String> inputSet;

	/**
	 * List of client handlers
	 */
	private ArrayList<ClientHandler> handlerList = new ArrayList<ClientHandler>();
	
	protected DeduplicationSocket(ServerSocket socket) throws IOException{
		this.socket = socket;
		
		// Call getInstance before creating any child threads to avoid thread safety issues
		this.inputSet = UniqueInputHash.getInstance();
	}

	/**
	 * Returns the number of clients currently connected to the socket
	 * 
	 * @return integer
	 */
	protected int numberOfConnections() {
		return handlerList.size();
	}
	
	/**
	 * Adds a new client and creates a handler for client input
	 * 
	 * @throws IOException
	 */
	protected void addNewClient() throws IOException{
		// Accept new client
		Client newClient = new Client(socket.accept());

		// Start new connection
		ClientHandler newHandler = new ClientHandler(newClient);
		synchronized (handlerList) {
			handlerList.add(newHandler);
		}
		newHandler.start();
	}
	
	/**
	 * Disconnects any clients that have entered invalid input
	 */
	protected void removeInvalidClients(){
		for(Iterator<ClientHandler> clientHandlerIterator = handlerList.iterator(); clientHandlerIterator.hasNext(); ) {
			ClientHandler handler = clientHandlerIterator.next();
			
			// Remove invalid clients
			if(!handler.clientIsConnected() && !handler.clientIsValid()){
				synchronized(handlerList){
					clientHandlerIterator.remove();
				}
			}
		}
	}
	
	/**
	 * Checks that all clients are connected. Removes any invalid connections
	 * 
	 * @return true if all clients are connected
	 */
	protected boolean allClientsConnected(){
		boolean allConnected = true;
		
		removeInvalidClients();
		
		for(Iterator<ClientHandler> clientHandlerIterator = handlerList.iterator(); clientHandlerIterator.hasNext(); ) {
			ClientHandler handler = clientHandlerIterator.next();
			
			// If one of the clients disconnected, stop running
			if(!handler.clientIsConnected()){
				allConnected = false;
			}
		}
		
		return allConnected;
	}
	
	/**
	 * Returns an iterator for all unique client input
	 * 
	 * @return Iterator<String>
	 */
	protected Iterator<String> getInputIterator(){
		return inputSet.iterator();
	}
	
	/**
	 * Disconnects the socket and all the children.
	 * @throws IOException
	 */
	protected void disconnect() throws IOException{
		disconnectAllClients();
		socket.close();		
	}
	
	/**
	 * Disconnects all clients and terminates their handlers
	 * @throws IOException
	 */
	private void disconnectAllClients() throws IOException{
		for(Iterator<ClientHandler> clientHandlerIterator = handlerList.iterator(); clientHandlerIterator.hasNext(); ) {
			ClientHandler handler = clientHandlerIterator.next();
			
			if(handler.clientIsConnected()){
				handler.terminate();
			}
		}
	}

}

