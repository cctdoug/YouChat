package youchat;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private ServerSocket serverSocket;
//	private Map<String, PrintWriter> connectedClients;
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
			if(serverSocket != null) {
				serverSocket.close();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	// a b Hi
	public synchronized void sendMessage(String sender, String receiver, String message) {
		PrintWriter writer = ClientHandler.connectedClients.get(receiver);
		// ab
		String conversationSender = sender + receiver;
		// ba
		String conversationReceiver = receiver + sender;
		List<String> conversation = new ArrayList<>();

		if (writer != null && ClientHandler.connectedClients.containsKey(receiver)) {
			// a : hi
			writer.println(sender + ": " + message);
			conversation.add(sender + ": " + message);
			if (messageHistory.containsKey(conversationSender) || messageHistory.containsKey(conversationReceiver)) {
				messageHistory.get(conversationSender).add(sender + ": " + message);

			} else {
				messageHistory.put(conversationSender, conversation);
				messageHistory.put(conversationReceiver, conversation);
			}

		} else {
			writer.println("User does not exist. Try again.");
			return;
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
