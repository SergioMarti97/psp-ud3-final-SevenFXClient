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
        void onResult(String result);
    }

    private OnBackgroundThreadListener listener;

    private boolean keepRunning;

    private boolean isMyTurn;

    private boolean isFinished;

    private Socket socket;

    private Timer timer;

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
                setTimerFinished();
                String message = objectInputStream.readUTF();

                if (isMyTurn && message.equals("receiveMoreCards")) {
                    Card card = (Card) objectInputStream.readObject();
                    Platform.runLater(() -> listener.onReceiveCard(card));
                    isMyTurn = false;
                } else if (message.equals("getTurn")) {
                    isMyTurn = objectInputStream.readBoolean();
                    Platform.runLater(() -> listener.onMyTurn(isMyTurn));
                } else if (message.equals("standUp")) {
                    isMyTurn = false;
                    isFinished = true;
                    timer = null;
                } else if (message.equals("getResult")) {
                    String result = objectInputStream.readUTF();
                    Platform.runLater(() -> listener.onResult(result));
                }

                if (isMyTurn && timer != null) {
                    timer.cancel();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            Platform.runLater(() -> listener.onDisconnectServer());
        }
    }

    private void setTimerTurn() {
        if (timer != null && isMyTurn) {
            return;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isFinished) {
                    return;
                }
                try {
                    objectOutputStream.writeUTF("getTurn");
                    objectOutputStream.flush();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }, 1000);
    }

    private void setTimerFinished() {
        if (timer != null && !isFinished) {
            return;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isMyTurn) {
                    return;
                }
                try {
                    objectOutputStream.writeUTF("getResult");
                    objectOutputStream.flush();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }, 1000);
    }

    public void standUp() throws IOException {
        objectOutputStream.writeUTF("standUp");
        objectOutputStream.flush();
        isMyTurn = false;
        isFinished = true;
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
