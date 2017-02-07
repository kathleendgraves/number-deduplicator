package server;
import java.io.FileWriter;
import java.io.IOException;

public class LogFile {
	FileWriter writer = null;
	String lineSeparator = System.getProperty("line.separator");
	
	public LogFile() throws IOException{
		writer = new FileWriter(System.getProperty("java.io.tmpdir") + "numbers.log");
	}
	
	public void writeToLog(String input) throws IOException{
		writer.append(input);
		writer.append(lineSeparator);
	}
	
	public void close() throws IOException{
		writer.flush();
		writer.close();
	}

}
