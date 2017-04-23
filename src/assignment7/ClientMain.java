package assignment7;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ClientMain extends Application {


	private ClientObserver writer;
	private BufferedReader reader;
	private AnchorPane anchorPane;
	private ArrayList<Node> elements;
	private TextField input;
	private TextArea chat;
	private Button sendButton;


	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			setUpNetworking();
			initView();
			primaryStage.setScene(new Scene(anchorPane, 535, 544));
			primaryStage.show();
			primaryStage.setOnCloseRequest(
					event -> {
						System.exit(0);
					}
					);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView(){
		anchorPane = new AnchorPane();
		elements = new ArrayList<Node>();
		
		input = new TextField();
		input.setPrefSize(510, 41);
		input.setPromptText("Enter message");
		elements.add(input);
		
		chat = new TextArea();
		chat.setPrefSize(510, 342);
		chat.setWrapText(true);
		elements.add(chat);
		
		sendButton = new Button();
		sendButton.setPrefSize(50, 27);
		sendButton.setText("Send");
		elements.add(sendButton);
		sendButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				writer.println(input.getText());
				writer.flush();
				input.clear();
				
			}
			
		});
		
		anchorPane.getChildren().addAll(elements);
		AnchorPane.setTopAnchor(sendButton, 452.0);
		AnchorPane.setLeftAnchor(sendButton, 467.0);
		AnchorPane.setTopAnchor(chat, 49.0);
		AnchorPane.setLeftAnchor(chat, 12.0);
		AnchorPane.setTopAnchor(input, 489.0);
		AnchorPane.setLeftAnchor(input, 12.0);
		
		
		
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket socket = new Socket("127.0.0.1", 4242);
		InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new ClientObserver(socket.getOutputStream());
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}



	public static void main(String args[]) {
		try {
			launch(args);
			new ClientMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					chat.appendText(message + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}