/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.youchat;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 @author 2020338 - Douglas Santos
 */
public class Server {

    private ServerSocket serverSocket;
    private static Map<String, List<String>> messageHistory;

    public Server() {
        messageHistory = new HashMap<>();
    }

    /**
     * Starts the server and listens for client connections on the specified
     * port.
     *
     * @param port: the port number to listen to
     */
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("Server started on port " + port);

            while (true) {
                // Wait for a client to connect
                Socket clientSocket = serverSocket.accept();
                // Create a new ClientHandler for the connected client
                new ClientHandler(clientSocket);
                System.out.println("New client connected: " + clientSocket);
                // Start a new thread to handle the client's requests
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Closes the server socket
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method that sends a message from the sender to the receiver.
     *
     * @param sender: the sender of the message
     * @param receiver: the receiver of the message
     * @param message: the message content
     */
    public synchronized void sendMessage(String sender, String receiver, String message) {
        // PrintWriter object associated with the receiver's username
        PrintWriter writer = ClientHandler.connectedClients.get(receiver);
        // A way to identify the message histories between two users
        // creates a unique identifier with both users names to add to the messageHistory map
        String conversationSender = sender + receiver;
        String conversationReceiver = receiver + sender;
        // Stores the message sent
        List<String> conversation = new ArrayList<>();

        // Send the message to the receiver
        writer.println(sender + ": " + message);
        // Add the message to the conversation history
        conversation.add(sender + ": " + message);
        // Check if a conversation between the two users already exists in the messageHistory
        if (messageHistory.containsKey(conversationSender) || messageHistory.containsKey(conversationReceiver)) {
            // if it does, it adds more conversations
            messageHistory.get(conversationSender).add(sender + ": " + message);
        } else {
            // if it doesn't it creates a new conversation history
            messageHistory.put(conversationSender, conversation);
            messageHistory.put(conversationReceiver, conversation);
        }
    }

    /**
     * Retrieves the conversation history between the user and the targetUser.
     *
     * @param user: the sender user
     * @param targetUser: the target user to retrieve the conversation history
     * with
     * @return the conversation history between the user and the targetUser
     */
    public synchronized List<String> getConversationHistory(String user, String targetUser) {
        List<String> conversationHistory = new ArrayList<>();

        // Check if the message history and the target user exist
        if (messageHistory != null && ClientHandler.connectedClients.containsKey(targetUser)) {
            conversationHistory = messageHistory.get(user + targetUser);
        }
        return conversationHistory;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(1234);
    }
}
