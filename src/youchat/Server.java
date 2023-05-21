package youchat;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private ServerSocket serverSocket;
	private static Map<String, List<String>> messageHistory;

	public Server() {
		messageHistory = new HashMap<>();
	}

	public void start(int port) {
		try {
			serverSocket = new ServerSocket(port);

			System.out.println("Server started on port " + port);

			while (true) {
				Socket clientSocket = serverSocket.accept();
				new ClientHandler(clientSocket);
				System.out.println("New client connected: " + clientSocket);

				Thread clientThread = new Thread(new ClientHandler(clientSocket));
				clientThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeServerSocket() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void sendMessage(String sender, String receiver, String message) {
		PrintWriter writer = ClientHandler.connectedClients.get(receiver);
		String conversationSender = sender + receiver;
		String conversationReceiver = receiver + sender;
		List<String> conversation = new ArrayList<>();

		writer.println(sender + ": " + message);
		conversation.add(sender + ": " + message);
		if (messageHistory.containsKey(conversationSender) || messageHistory.containsKey(conversationReceiver)) {
			messageHistory.get(conversationSender).add(sender + ": " + message);
		} else {
			messageHistory.put(conversationSender, conversation);
			messageHistory.put(conversationReceiver, conversation);
		}
	}

	public synchronized List<String> getConversationHistory(String username, String targetUser) {
		List<String> conversationHistory = new ArrayList<>();

		if (messageHistory != null && ClientHandler.connectedClients.containsKey(targetUser)) {
			conversationHistory = messageHistory.get(username + targetUser);
		}
		return conversationHistory;
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start(1234);
	}
}
