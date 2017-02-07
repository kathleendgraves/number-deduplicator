package server;

import java.util.HashSet;
import java.util.Iterator;

public class UniqueInputHash {

	private static HashSet<String> instance = null;
	private static long duplicateCount = 0;
	private static long uniqueCount = 0;

	protected UniqueInputHash() {
		// No instantiation.
	}

	// Must be called before creating client threads
	public static HashSet<String> getInstance() {
		if (instance == null) {
			instance = new HashSet<String>();
		}
		return instance;
	}
	
	public static long getDuplicateCount(){
		return duplicateCount;
	}
	
	public static synchronized void incrementDuplicates(){
		duplicateCount++;
	}
	
	public static synchronized void resetDuplicates(){
		duplicateCount = 0;
	}
	
	public static long getUniqueCount(){
		return uniqueCount;
	}
	
	public static synchronized void incrementUniques(){
		uniqueCount++;
	}
	
	public static synchronized void resetUniques(){
		uniqueCount = 0;
	}

	public Iterator<String> iterator() {
		return instance.iterator();
	}

}
