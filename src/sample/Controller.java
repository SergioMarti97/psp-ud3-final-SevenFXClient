package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.Socket;

public class Controller implements BackgroundThread.OnBackgroundThreadListener  {

    @FXML
    private TextField txtFieldAddress;

    @FXML
    private TextField txtFieldPort;

    @FXML
    private Button btnConnectOrDisconnect;

    private BackgroundThread backgroundThread;

    @FXML
    public void initialize() {

    }

    @FXML
    void handleConnectOrDisconnect() throws IOException {
        if (backgroundThread == null) {
            backgroundThread = new BackgroundThread(txtFieldAddress.getText(), Integer.parseInt(txtFieldPort.getText()), this);
            backgroundThread.start();
            backgroundThread.send("Hi Client");
            return;
        }
        backgroundThread.doStop();
    }

    @Override
    public void onConnectServer(Socket socket) {
        txtFieldAddress.setDisable(true);
        txtFieldPort.setDisable(true);
        btnConnectOrDisconnect.setText("Disconnect");
    }

    @Override
    public void onDisconnectServer() {
        txtFieldAddress.setDisable(false);
        txtFieldPort.setDisable(false);
        btnConnectOrDisconnect.setText("Connect");
    }

    @Override
    public void onReceiveMessage(String message) {
        System.out.println(message);
    }

}
