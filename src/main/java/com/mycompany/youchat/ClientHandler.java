/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.youchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  2020338 - Douglas Santos
 */

public class ClientHandler implements Runnable {

    // Map to store the connected clients
    public static Map<String, PrintWriter> connectedClients = new HashMap<>();
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    Server server = new Server();

    public ClientHandler(Socket socket) {
        clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // Create BufferedReader to read messages from the client
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // Create PrintWriter to send messages to the client
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            writer.println("Please, enter your username:");
            username = reader.readLine();
            // Add the client's PrintWriter to the connectedClients map with the username as
            // the key
            connectedClients.put(username, writer);

            writer.println("Welcome " + username);
            System.out.println("User '" + username + "' has connected...");

            String clientMessage;
            /**
             * /<command> (/exit, /msg, /users, history) User commands that
             * controls the chat functionalities
             *
             */
            while ((clientMessage = reader.readLine()) != null) {
                // This command exits the chat and closes the connection
                if (clientMessage.equals("/exit")) {
                    break;
                }
                // This command send messages to target users
                // /msg <user> <message>
                if (clientMessage.startsWith("/msg")) {
                    // splits the user's message into 3 parts to identify it
                    String[] parts = clientMessage.split(" ", 3);
                    // If successfully split, it creates variables to hold the target user and
                    // message
                    // and verifies if the target is connected/exists.
                    if (parts.length == 3) {
                        String receiver = parts[1];
                        String message = parts[2];
                        // if the target doesn't exist, it will prompt an error and ask the user to try
                        // again
                        if (!connectedClients.containsKey(receiver)) {
                            writer.println("Invalid user. It does not exist or is offline. Please, try again.");
                        } else {
                            // if it does, it will call the method to send message and pass the details
                            server.sendMessage(username, receiver, message);
                        }
                        // if the message can't be split into 3 parts, something is missing
                    } else {
                        // Inform the client about the invalid command format
                        writer.println("Invalid message format. Usage: /msg <receiver> <message>");
                    }
                    // This command displays a list of connected users
                } else if (clientMessage.equals("/users")) {
                    List<String> users = new ArrayList<>(connectedClients.keySet());
                    writer.println("Connected users: " + users.toString());
                    // This command brings the message history between two users
                } else if (clientMessage.startsWith("/history")) {
                    String[] parts = clientMessage.split(" ", 2);
                    if (parts.length == 2) {
                        String targetUser = parts[1];
                        // it uses the same splitting technique to get the target user's username and
                        // pass it to the method
                        List<String> history = server.getConversationHistory(username, targetUser);
                        writer.println("Conversation history with " + targetUser + ":");

                        for (String message : history) {
                            writer.println(message);
                        }
                    } else {
                        // Inform the client about the invalid command format
                        writer.println("Invalid command format. Usage: /history <username>");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (username != null) {
                // Remove the client's username from the connectedClients map
                connectedClients.remove(username);
                // Print the user's disconnection to the server console
                System.out.println(username + " has disconnected");
            }
            try {
                // Close the client socket
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
