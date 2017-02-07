package server;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.TimerTask;

/**
 * Class ReportTask prints a report to standard output
 * regarding the input received
 * @author Kathleen
 *
 */
public class ReportTask extends TimerTask {
	
	/**
	 * The message to be formatted
	 */
	private static String reportMessage = "Received {0} unique numbers, {1} duplicates. Unique total: {2}";

	/**
	 * Prints a report using the singleton unique input list. 
	 */
	public void run() {
		HashSet<String> inputSet = UniqueInputHash.getInstance();
		
		String formattedMessage = MessageFormat.format(reportMessage, UniqueInputHash.getUniqueCount(), UniqueInputHash.getDuplicateCount(), inputSet.size());
		System.out.println(formattedMessage);
		UniqueInputHash.resetDuplicates();
		UniqueInputHash.resetUniques();
	}

}
