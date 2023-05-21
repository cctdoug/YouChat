package youchat;

import java.io.BufferedReader;
import java.io.IOException;

public class MessageReader implements Runnable {
    
	private BufferedReader reader;
	
    /**
     * Builds a MessageReader object with the specified BufferedReader.
     * 
     * @param reader: the BufferedReader
     */
    public MessageReader(BufferedReader reader) {
		super();
		this.reader = reader;
	}


    /**
     * Reads messages from the BufferedReader and prints them to the console.
     * Listens for messages until an IOException occurs or the connection is closed.
     */    
	@Override
    public void run() {
        try {
            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}