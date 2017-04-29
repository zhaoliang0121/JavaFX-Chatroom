package assignment7;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
	private TextArea userList;
	private MenuBar menu;
	private ComboBox<String> color;
	private ComboBox<String> kaomoji;
	private TextInputDialog usernamePop;
	private String username;
	public String result;
	private boolean accept;
	private ArrayList<String> localList;
	private Alert alert;

	@Override
	public void start(Stage primaryStage) throws Exception {

		localList = new ArrayList<String>();
		setUpNetworking();
		accept = false;
		while(accept == false){
			usernameInput();
		}
		initView();
		primaryStage.setScene(new Scene(anchorPane, 770, 401));
		primaryStage.setTitle("Log in as: " + username);
		primaryStage.show();
		primaryStage.setOnCloseRequest(event -> {
			System.exit(0);
		});
	}

	private void usernameInput() {
		usernamePop = new TextInputDialog();
		usernamePop.setTitle("Enter username");
		usernamePop.setHeaderText("Enter a user name you prefer");
		usernamePop.setContentText("Enter:");
		Optional<String> input = usernamePop.showAndWait();
		if (input.isPresent()) {
			try {
				username = input.get();
				writer.update(null,username);
				if(localList.contains(username)){
					accept = false;
					usernameAlert();
					System.exit(0);
				}
				else{
					accept = true;
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.exit(1);
		}
	}

	private void usernameAlert(){
		alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Duplicate Username!");
		alert.setHeaderText("This username has been used. Please restart the program to enter a new name.");
		alert.showAndWait();
		
		
	}
	private void initView() {
		anchorPane = new AnchorPane();
		elements = new ArrayList<Node>();

		menu = new MenuBar();
		menu.setPrefSize(770, 32);
		Menu file = new Menu("File");
		MenuItem item = new MenuItem("Close");
		item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.exit(0);

			}

		});
		file.getItems().add(item);
		menu.getMenus().add(file);
		Menu help = new Menu("Help");
		MenuItem item2 = new MenuItem("About");
		help.getItems().add(item2);
		menu.getMenus().add(help);
		elements.add(menu);

		input = new TextField();
		input.setPrefSize(524, 76);
		input.setPromptText("Enter message");
		input.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					writer.update(null, username + ": " + input.getText());
					input.clear();
				}
			}
		});
		elements.add(input);

		chat = new TextArea();
		chat.setPrefSize(586, 269);
		chat.setWrapText(true);
		elements.add(chat);

		userList = new TextArea();
		userList.setPrefSize(161, 269);
		userList.setWrapText(true);
		elements.add(userList);

		sendButton = new Button();
		sendButton.setPrefSize(61, 76);
		sendButton.setText("Send");
		elements.add(sendButton);
		sendButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				writer.update(null, username + ": " + input.getText());
				input.clear();

			}

		});

		color = new ComboBox<String>();
		color.getItems().addAll("Red", "Blue", "Green", "Black");
		color.setPromptText("Select a color");
		color.setPrefSize(161, 31);
		elements.add(color);

		kaomoji = new ComboBox<String>();
		kaomoji.setPrefSize(161, 31);
		kaomoji.getItems().addAll("ヽ(`⌒´メ)ノ", "(눈_눈)", "(◕‿◕)♡", "ლ(¯ロ¯ლ)");
		kaomoji.setPromptText("Pick your emoji!~");
		kaomoji.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				input.setText(input.getText() + newValue);
			}
		});
		elements.add(kaomoji);

		anchorPane.getChildren().addAll(elements);
		AnchorPane.setTopAnchor(menu, 0.0);
		AnchorPane.setLeftAnchor(menu, 0.0);
		AnchorPane.setTopAnchor(userList, 36.0);
		AnchorPane.setLeftAnchor(userList, 602.0);
		AnchorPane.setTopAnchor(color, 314.0);
		AnchorPane.setLeftAnchor(color, 602.0);
		AnchorPane.setTopAnchor(kaomoji, 352.0);
		AnchorPane.setLeftAnchor(kaomoji, 602.0);
		AnchorPane.setTopAnchor(sendButton, 314.0);
		AnchorPane.setLeftAnchor(sendButton, 535.0);
		AnchorPane.setTopAnchor(chat, 36.0);
		AnchorPane.setLeftAnchor(chat, 10.0);
		AnchorPane.setTopAnchor(input, 314.0);
		AnchorPane.setLeftAnchor(input, 10.0);

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			
			try {
				while ((message = reader.readLine()) != null) {
					if (accept) {
						displayMessage(chat, message);
					} else
						localList.add(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void displayMessage(TextArea chat, String input) {
		String intendedName;
		String message;
		String[] split = input.split(" ");
		String name = split[0].substring(0, split[0].length() - 1);
		if (split[1] != null && split[1].substring(0, 1).equals("@")) {
			intendedName = split[1].substring(1, split[1].length() - 1);
			Integer beginMessage = name.length() + 3 + intendedName.length() + 2;
			message = name + ": " + input.substring(beginMessage, input.length());
			if (username.equals(intendedName) || username.equals(name)) {
				chat.appendText("PRIVATE " + message + "\n");
			}
			return;
		} else
			chat.appendText(input + "\n");
	}
}
