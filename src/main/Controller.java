package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import main.card.Card;

import java.io.IOException;
import java.net.Socket;

public class Controller implements BackgroundThread.OnBackgroundThreadListener  {

    @FXML
    private Label lbTextStatus;

    @FXML
    private TextField txtFieldAddress;

    @FXML
    private TextField txtFieldPort;

    @FXML
    private Button btnConnectOrDisconnect;

    @FXML
    private HBox gameBox;

    @FXML
    private ListView<Card> listCards;

    @FXML
    private Label lbTotalPoints;

    private BackgroundThread backgroundThread;

    @FXML
    public void initialize() {
        lbTextStatus.setDisable(true);
        lbTextStatus.setVisible(false);
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

    @FXML
    void handleReceiveMoreCards() throws IOException {
        backgroundThread.receiveMoreCards();
    }

    @FXML
    void handleStandUp() throws IOException {
        backgroundThread.standUp();
    }

    @Override
    public void onConnectServer(Socket socket) {
        txtFieldAddress.setDisable(true);
        txtFieldPort.setDisable(true);
        btnConnectOrDisconnect.setText("Disconnect");
        gameBox.setDisable(false);
        gameBox.setVisible(true);

        lbTextStatus.setText("Wait your turn...");
        lbTextStatus.setDisable(false);
        lbTextStatus.setVisible(true);
    }

    @Override
    public void onDisconnectServer() {
        backgroundThread = null;
        txtFieldAddress.setDisable(false);
        txtFieldPort.setDisable(false);
        btnConnectOrDisconnect.setText("Connect");
        gameBox.setDisable(true);
        gameBox.setVisible(false);

        lbTextStatus.setText(null);
        lbTextStatus.setDisable(true);
        lbTextStatus.setVisible(false);

        listCards.getItems().clear();
        lbTotalPoints.setText("0");
    }

    @Override
    public void onReceiveCard(Card card) {
        try {
            listCards.getItems().add(card);
            float points = Float.parseFloat(lbTotalPoints.getText()) + card.getSymbol().getPoints();
            lbTotalPoints.setText(String.valueOf(points));

            if (points == 7.5) {
                backgroundThread.standUp();
                lbTextStatus.setText("Waiting for the players to finish...");
            } else if (points > 7.5) {
                backgroundThread.standUp();
                lbTextStatus.setText("You lost");
            }

            lbTextStatus.setDisable(false);
            lbTextStatus.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMyTurn(boolean isMyTurn) {
        if (isMyTurn) {
            lbTextStatus.setText(null);
            lbTextStatus.setDisable(true);
            lbTextStatus.setVisible(false);
            return;
        }

        lbTextStatus.setText("Wait your turn...");
        lbTextStatus.setDisable(false);
        lbTextStatus.setVisible(true);
    }

    @Override
    public void onResult(String result) {
        switch (result) {
            case "draw":
                lbTextStatus.setText("Draw");
                break;
            case "win":
                lbTextStatus.setText("You Win!");
                break;
            case "lose":
                lbTextStatus.setText("You lose!");
                break;
            default:
                lbTextStatus.setText("Waiting for the players to finish...");
                break;
        }

        lbTextStatus.setDisable(false);
        lbTextStatus.setVisible(true);
    }

}
