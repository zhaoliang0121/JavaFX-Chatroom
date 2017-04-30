/* ChatClient ClientMain.java
 * EE422C Project 7 submission by
 * Zhaofeng Liang
 * zl4685
 * 16230
 * Zohaib Imam
 * szi58
 * 16230
 * Slip days used: 1
 * https://github.com/iamzoh/assignment7
 * Spring 2017
 */
package assignment7;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientMain extends Application {

	private ClientObserver writer;
	private BufferedReader reader;
	private AnchorPane anchorPane;
	private ArrayList<Node> elements;
	private TextField input;
	private TextArea chat;
	private Button sendButton;
	private TextArea privateChat;
	private MenuBar menu;
	private ComboBox<String> color;
	private ComboBox<String> kaomoji;
	private TextInputDialog usernamePop;
	private String username;
	private Text publicT;
	private Text privateT;
	private boolean accept;
	private ArrayList<String> localList;
	private Alert alert;
    private Scene scene;

	@Override
	public void start(Stage primaryStage) throws Exception {

		localList = new ArrayList<String>();
		setUpNetworking();
		accept = false;
		while(accept == false){
			usernameInput();
		}
		initView();
        scene = new Scene(anchorPane, 770, 401);
        //scene.getStylesheets().add("assignment7/Blue.css");
		primaryStage.setScene(scene);
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


    @SuppressWarnings("not resolve")
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
		chat.setPrefSize(374, 246);
		chat.setWrapText(true);
        chat.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        chat.setEditable(false);
		elements.add(chat);

		privateChat = new TextArea();
        privateChat.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
		privateChat.setPrefSize(374, 246);
		privateChat.setWrapText(true);
		elements.add(privateChat);
		
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
		color.getItems().addAll("Red", "Blue");
		color.setPromptText("Select a color");
		color.setPrefSize(161, 31);
        color.valueProperty().addListener((observable, oldValue, newValue) -> {

            switch(newValue){
                case "Red": {
                    scene.getStylesheets().remove("assignment7/Blue.css");
                    scene.getStylesheets().add("assignment7/Red.css");
                    break;
                }
                case "Blue": {
                    scene.getStylesheets().remove("assignment7/Red.css");
                    scene.getStylesheets().add("assignment7/Blue.css");
                    break;
                }
            }
        });
		elements.add(color);

		kaomoji = new ComboBox<String>();
		kaomoji.setPrefSize(161, 31);
		kaomoji.getItems().addAll("ヽ(`⌒´メ)ノ", "(눈_눈)", "(◕‿◕)♡", "ლ(¯ロ¯ლ)","ლ(ಠ_ಠ ლ","¯\\_(ツ)_/¯","ヾ(☆'∀'☆)",
				"(⊃｡•́‿•̀｡)⊃","┌∩┐(◣_◢)┌∩┐","٩(╬ʘ益ʘ╬)۶","(╯°□°)╯︵ ┻━┻ ","ε=ε=ε=ε=┌(;￣▽￣)┘","ʕ •ᴥ• ʔ?");
		kaomoji.setPromptText("Pick your emoji!~");
		kaomoji.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				input.setText(input.getText() + newValue);
			}
		});
		elements.add(kaomoji);
		
		publicT = new Text();
		publicT.setText("Public Chat");
		elements.add(publicT);
		
		privateT = new Text();
		privateT.setText("Private Chat");
		elements.add(privateT);

		anchorPane.getChildren().addAll(elements);
		AnchorPane.setTopAnchor(menu, 0.0);
		AnchorPane.setLeftAnchor(menu, 0.0);
		AnchorPane.setTopAnchor(privateChat, 59.0);
		AnchorPane.setLeftAnchor(privateChat, 389.0);
		AnchorPane.setTopAnchor(color, 314.0);
		AnchorPane.setLeftAnchor(color, 602.0);
		AnchorPane.setTopAnchor(kaomoji, 352.0);
		AnchorPane.setLeftAnchor(kaomoji, 602.0);
		AnchorPane.setTopAnchor(sendButton, 314.0);
		AnchorPane.setLeftAnchor(sendButton, 535.0);
		AnchorPane.setTopAnchor(chat, 59.0);
		AnchorPane.setLeftAnchor(chat, 10.0);
		AnchorPane.setTopAnchor(input, 314.0);
		AnchorPane.setLeftAnchor(input, 10.0);
		AnchorPane.setTopAnchor(publicT, 45.0);
		AnchorPane.setLeftAnchor(publicT,10.0);
		AnchorPane.setTopAnchor(privateT, 45.0);
		AnchorPane.setLeftAnchor(privateT, 389.0);
		

	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
        //Socket socket = new Socket("10.146.28.178", 4242);
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
	
	private void displayMessage(TextArea chat, String input){
		
		Media sound = new Media(new File("receive.mp3").toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(sound);
		Media sound2 = new Media(new File("private.mp3").toURI().toString());
		MediaPlayer mediaPlayer2 = new MediaPlayer(sound2);
        String intendedName;
        String message;
	    String[] split = input.split(" ");
        String name = split[0].substring(0, split[0].length()-1);
	    if(split[1] != null && split[1].substring(0,1).equals("@")){
	        intendedName = split[1].substring(1,split[1].length()-1);
	        Integer beginMessage = name.length() + 3 + intendedName.length() + 2;
            message = name + ": " +input.substring(beginMessage, input.length());
            if(username.equals(intendedName) || username.equals(name)){
                privateChat.appendText(new SimpleDateFormat("EEEEE, MMMMM d h:mm a").format(Calendar.getInstance().getTime()));
				privateChat.appendText("\n"+ "PRIVATE " + message + "\n" + "\n");
				if(!username.equals(name)){
					mediaPlayer2.play();
				}
            }
            return;
        }
        else{
        	
        	chat.appendText(new SimpleDateFormat("EEEEE, MMMMM " + "d h:mm a").format(Calendar.getInstance().getTime()));
			chat.appendText("\n" + input + "\n" + "\n");
			if(!username.equals(name)){
				mediaPlayer.play();
			}
	}
    }
}
