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
 * @author 2020338 - Douglas Santos
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

            // Header - YouChat
            writer.println("[----------- YouChat <Y> Instant Messenger -----------]");
            writer.println("Choose your username to start using YouChat:");
            username = reader.readLine();
            //In case the user types /exit before choosing a username
            if (username == null) {
                // Server message
                System.out.println("Client disconnected without creating a username");
            } else {
                username.toUpperCase().trim();
                // Checks to see if the username already exists (ignores case)
                while (connectedClients.containsKey(username)) {
                    writer.println("\nUh, I'm sorry. This username is already being used.\n"
                            + "Choose a different one and try again!\n");
                    username = reader.readLine().toUpperCase().trim();
                }
                // Add the client's PrintWriter to the connectedClients map with the username as
                // the key
                connectedClients.put(username, writer);

                // Welcome and instructions
                writer.println("\nWelcome to YouChat, " + username + "!!!");
                writer.println("These are the commands you can use in this prototype:\n"
                        + " /msg <receiver> <message> - to send a message, \n"
                        + " /users - to check who is online,\n"
                        + " /history <user> - to check the history of conversations with the desired <user>\n"
                        + " /exit - to leave YouChat (Remember to say 'bye' to your friends!). \n\n"
                        + ">>> - - Start here! Enjoy it! - - >>>\n"
                        + "[----------- YouChat <Y> Instant Messenger -----------]\n");
                // Server message
                System.out.println("User '" + username + "' has connected...");
            }
            String originalClientMessage;
            String clientMessageLowerCase;
            /**
             * /<command> (/exit, /msg, /users, /history) User commands that
             * controls the chat functionalities
             *
             */
            while ((originalClientMessage = reader.readLine()) != null) {
                clientMessageLowerCase = originalClientMessage.toLowerCase();
                // This command exits the chat and closes the connection
                if (clientMessageLowerCase.equals("/exit")) {
                    break;
                }
                // This command send messages to target users
                // /msg <user> <message>
                if (clientMessageLowerCase.startsWith("/msg")) {
                    // splits the user's message into 3 parts to identify it
                    String[] parts = originalClientMessage.split(" ", 3);
                    // If successfully split, it creates variables to hold the target user and
                    // message
                    // and verifies if the target is connected/exists.
                    if (parts.length == 3) {
                        String receiver = parts[1].toUpperCase().trim();
                        String message = parts[2];
                        // if the target doesn't exist, it will prompt an error and ask the user to try
                        // again
                        if (!connectedClients.containsKey(receiver)) {
                            writer.println("Invalid username. Seems like '" + receiver + "' may not exist or be offline at the moment.\n"
                                    + "Check the username you typed or the online users with /users command!\n");
                        } else {
                            // if it does, it will call the method to send message and pass the details
                            server.sendMessage(username, receiver, message);
                        }
                        // if the message can't be split into 3 parts, something is missing
                    } else {
                        // Inform the client about the invalid command format
                        writer.println("Invalid message format.\n Remember the format: /msg <receiver> <message>\n");
                    }
                    // This command displays a list of connected users
                } else if (clientMessageLowerCase.equals("/users")) {
                    List<String> users = new ArrayList<>(connectedClients.keySet());
                    writer.println("People online now: " + users.toString());
                    if (!users.isEmpty()) {
                        writer.println("Remember how to talk to them\n "
                                + "Ex: /msg " + users.get(0) + " Hello, there!\n");
                    }
                    // This command brings the message history between two users
                } else if (clientMessageLowerCase.startsWith("/history")) {
                    String[] parts = clientMessageLowerCase.split(" ", 2);
                    if (parts.length == 2) {
                        String targetUser = parts[1].toUpperCase().trim();
                        if (targetUser.equals(username)) {
                            writer.println("Oops! Seems like you are trying to retrieve a conversation history\n"
                                    + "with yourself. That won't work! Try again.\n");
                        } else if (connectedClients.containsKey(targetUser)) {
                            // it uses the same splitting technique to get the target user's username and
                            // pass it to the method
                            List<String> history = server.getConversationHistory(username, targetUser);
                            writer.println(" - - - - - Conversation history with '" + targetUser + "' - - - - -\n");
                            for (String message : history) {
                                writer.println(message);
                            }
                            writer.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
                        } else {
                            writer.println("Invalid username. '" + targetUser + "' may not exist or you two haven't talked yet.\n"
                                    + "Check the username you typed or the online users with /users command!\n");
                        }

                    } else {
                        // Inform the client about the invalid command format
                        writer.println("Invalid command format.\n Remember the format: /history <username>\n");
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
                System.out.println("'" + username + "' has disconnected");
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
