package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.Socket;

public class Controller implements BackgroundThread.OnBackgroundThreadListener  {

    @FXML
    private TextField txtFieldAddress;

    @FXML
    private TextField txtFieldPort;

    @FXML
    private Button btnConnectOrDisconnect;

    @FXML
    private HBox gameBox;

    private BackgroundThread backgroundThread;

    @FXML
    public void initialize() {
        gameBox.setDisable(true);
        gameBox.setVisible(false);
    }

    @FXML
    void handleConnectOrDisconnect() throws IOException {
        if (backgroundThread == null) {
            backgroundThread = new BackgroundThread(txtFieldAddress.getText(), Integer.parseInt(txtFieldPort.getText()), this);
            backgroundThread.start();
            return;
        }
        backgroundThread.doStop();
    }

    @Override
    public void onConnectServer(Socket socket) {
        txtFieldAddress.setDisable(true);
        txtFieldPort.setDisable(true);
        btnConnectOrDisconnect.setText("Disconnect");
        gameBox.setDisable(false);
        gameBox.setVisible(true);
    }

    @Override
    public void onDisconnectServer() {
        backgroundThread = null;
        txtFieldAddress.setDisable(false);
        txtFieldPort.setDisable(false);
        btnConnectOrDisconnect.setText("Connect");
        gameBox.setDisable(true);
        gameBox.setVisible(false);
    }

    @Override
    public void onReceiveMessage(String message) {
        System.out.println(message);
    }

}
