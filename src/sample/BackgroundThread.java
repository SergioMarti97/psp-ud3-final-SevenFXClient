package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BackgroundThread extends Thread {

    public  interface OnBackgroundThreadListener {
        void onConnectServer(Socket socket) throws IOException;
        void onDisconnectServer();
        void onReceiveMessage(String message);
    }

    private OnBackgroundThreadListener listener;

    private boolean keepRunning;

    private Socket socket;

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
                String message = objectInputStream.readUTF();
                listener.onReceiveMessage(message);
            }
            listener.onDisconnectServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) throws IOException {
        objectOutputStream.writeUTF(message);
        objectOutputStream.flush();
    }

    public void doStop() {
        this.keepRunning = false;
        this.listener.onDisconnectServer();
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

}
