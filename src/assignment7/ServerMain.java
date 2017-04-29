package assignment7;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Scanner;

public class ServerMain extends Observable {

	HashMap<String, User> Names = new HashMap<>();
	ArrayList<String> UserList;

	public static void main(String[] args) {
		try {
			new ServerMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		UserList = new ArrayList<String>();
		while (true) {
			Socket clientSocket = serverSock.accept();

			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			ClientHandler handler = new ClientHandler(clientSocket);

			Thread t = new Thread(handler);
			t.start();
			this.addObserver(writer);
			System.out.println("got a connection");
		}
	}

	class ClientHandler implements Runnable {
		private BufferedReader reader;
		private ClientObserver writer;
		private String username;

		public ClientHandler(Socket clientSocket) throws IOException {
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new ClientObserver(sock.getOutputStream());
			username = "";
		}

		public void run() {
			for (String n : UserList) {
				writer.update(null, n);
			}
			String message;
			String name;
			while (true) {
				try {
					name = reader.readLine();
					if (name == null) {
						return;
					}
					if (name.contains(":")) {
						break;
					}
					if (!(UserList.contains(name))) {
						UserList.add(name);
						System.out.println(UserList.toString());
						username = name;

					} else {
						System.out.println("name existed");
						break;
					}

				}

				catch (Exception e) {

				}

			}

			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("server read " + message);
					setChanged();
					notifyObservers(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
