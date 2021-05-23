package main;

import javafx.application.Platform;
import main.card.Card;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundThread extends Thread {

    public  interface OnBackgroundThreadListener {
        void onConnectServer(Socket socket) throws IOException;
        void onDisconnectServer();
        void onReceiveCard(Card card);
        void onMyTurn(boolean isMyTurn);
    }

    private OnBackgroundThreadListener listener;

    private boolean keepRunning;

    private boolean isMyTurn;

    private Socket socket;

    private Timer timerTurn;

    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;

    public BackgroundThread(String hostName, int port, OnBackgroundThreadListener listener) throws IOException {
        this.socket = new Socket(hostName, port);
        this.objectOutputStream =  new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        this.keepRunning = true;
        this.listener = listener;
        this.listener.onConnectServer(socket);
    }

    @Override
    public void run() {
        try {
            while (keepRunning) {
                setTimerTurn();
                String message = objectInputStream.readUTF();
                if (isMyTurn && message.equals("receiveMoreCards")) {
                    Card card = (Card) objectInputStream.readObject();
                    Platform.runLater(() -> listener.onReceiveCard(card));
                    isMyTurn = false;
                } else if (message.equals("getTurn")) {
                    isMyTurn = objectInputStream.readBoolean();
                    Platform.runLater(() -> listener.onMyTurn(isMyTurn));
                }

                if (isMyTurn && timerTurn != null) {
                    timerTurn.cancel();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Platform.runLater(() -> listener.onDisconnectServer());
        }
    }

    private void setTimerTurn() {
        if (isMyTurn) {
            return;
        }

        timerTurn = new Timer();
        timerTurn.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    objectOutputStream.writeUTF("getTurn");
                    objectOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    public void receiveMoreCards() throws IOException {
        objectOutputStream.writeUTF("receiveMoreCards");
        objectOutputStream.flush();
    }

    public void doStop() throws IOException {
        objectOutputStream.writeUTF("disconnect");
        objectOutputStream.flush();
        keepRunning = false;
    }

}
