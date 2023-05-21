package youchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {

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
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			writer = new PrintWriter(clientSocket.getOutputStream(), true);

			writer.println("Please, enter your username:");
			username = reader.readLine();
			connectedClients.put(username, writer);
			writer.println("Welcome " + username);
			System.out.println("User" + username + "connected...");

			String clientMessage;
			while ((clientMessage = reader.readLine()) != null) {
				if (clientMessage.equals("/exit")) {
					break;
				}
				if (clientMessage.startsWith("/msg")) {
					String[] parts = clientMessage.split(" ", 3);
					if (parts.length == 3) {
						String receiver = parts[1];
						String message = parts[2];
						server.sendMessage(username, receiver, message);
					} else {
						writer.println("Invalid message format. Usage: /msg <receiver> <message>");
					}
				} else if (clientMessage.equals("/users")) {
					List<String> users = new ArrayList<>(connectedClients.keySet());
					writer.println("Connected users: " + users.toString());
				} else if (clientMessage.startsWith("/history")) {
					String[] parts = clientMessage.split(" ", 2);
					if (parts.length == 2) {
						String targetUser = parts[1];
						List<String> history = server.getConversationHistory(username, targetUser);
						writer.println("Conversation history with " + targetUser + ":");
						for (String message : history) {
							writer.println(message);
						}
					} else {
						writer.println("Invalid command format. Usage: /history <username>");
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (username != null) {
				connectedClients.remove(username);
				System.out.println(username + " has disconnected");
			}
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
