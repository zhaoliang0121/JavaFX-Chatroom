package assignment7;




import java.io.*;
import java.net.Socket;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class ClientMain{

    @FXML
    private Button Send;

    @FXML
    private TextArea chatHistory;

    @FXML
    private TextField tfSend;


    private PrintWriter writer;
    private BufferedReader reader;


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
}
