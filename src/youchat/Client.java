package youchat;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public void connect(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + serverAddress + ":" + serverPort);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            Thread messageReaderThread = new Thread(new MessageReader(reader));
            messageReaderThread.start();

            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                if (userInput.equals("/exit")) {
                    break;
                }
                writer.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.connect("localhost", 1234);
    }
}
