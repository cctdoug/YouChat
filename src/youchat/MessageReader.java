package youchat;

import java.io.BufferedReader;
import java.io.IOException;

public class MessageReader implements Runnable {
    
	private BufferedReader reader;
	
	
    public MessageReader(BufferedReader reader) {
		super();
		this.reader = reader;
	}


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