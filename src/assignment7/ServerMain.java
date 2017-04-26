package assignment7;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;

public class ServerMain extends Observable {

    HashMap<String, User> Names = new HashMap<>();

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
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
            ClientHandler handler = new ClientHandler(clientSocket);
            Thread t = new Thread(handler);
			t.start();
			this.addObserver(writer);
            synchronized (handler) {
               handler.getName();
            }
			System.out.println("got a connection");
		}
	}

	class ClientHandler implements Runnable {
		private BufferedReader reader;
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		public ClientHandler(Socket clientSocket) {
			Socket sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				oos = new ObjectOutputStream(sock.getOutputStream());
				ois = new ObjectInputStream(sock.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

		public void getName() throws IOException{
            String message;
            synchronized (this) {
                try {
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                        User check = new User(message);
                        if (!Names.containsKey(message)) {
                            Names.put(message, check);
                            System.out.println(Names.size());
                           // User newUser = new User(message);
                           // oos.writeObject(newUser);
                           // oos.flush();
                            return;
                        } else {
                        	oos.writeObject(null);
                        	oos.flush();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
            }
        }

        public void run() {
			String message;
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
