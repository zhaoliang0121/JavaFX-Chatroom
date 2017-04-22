package assignment7;




import java.io.*;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class ClientMain extends Application {

    @FXML
    private Button Send;

    @FXML
    private TextArea chatHistory;

    @FXML
    private TextField tfSend;


    private PrintWriter writer;
    private BufferedReader reader;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ChatRoom.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Chat Room");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setUpNetworking() throws Exception {
        Socket socket = new Socket("127.0.0.1", 5000);
        InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(socket.getOutputStream());
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
    }

    private class IncomingReader implements Runnable {
        public void run(){
            String message;
            try{
                while((message = reader.readLine()) != null){
                    chatHistory.appendText(message + "\n");
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]){
        try {
            new ClientMain().setUpNetworking();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
