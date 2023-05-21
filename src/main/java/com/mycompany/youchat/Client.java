/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.youchat;

import java.io.*;
import java.net.*;

/**
 *
 * @author 2020338 - Douglas Santos
 */
public class Client {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    /**
     * Connects the client to the specified server address and port.
     *
     * @param serverAddress: the server address to connect to
     * @param serverPort: the server port to connect to
     */
    public void connect(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + serverAddress + ":" + serverPort);

            // Set up the input and output streams for communication with the server
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Start a separate thread to read messages from the server
            Thread messageReaderThread = new Thread(new MessageReader(reader));
            messageReaderThread.start();

            // Read user input from the console and send it to the server
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            // Read user input from the console until it is null (indicating end of input)
            while ((userInput = userInputReader.readLine()) != null) {
                // If the user enters "/exit", break out of the loop
                // Terminates the input loop and stop sending messages to the server
                if (userInput.equals("/exit")) {
                    break;
                }
                // Send the user's input to the server by writing it to the PrintWriter
                writer.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the socket to release the resources associated with it.
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
