package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private final int port = 9092;
    private ServerSocket server;

    public Server() throws IOException {
        this.server = new ServerSocket(port);
        server.setReuseAddress(true);
    }

    @Override
    public void run() {
        handleConnection();
    }

    private void handleConnection() {
        try {
            while (true) {
                Socket client = server.accept();
                System.out.println("SERVER, message: New connection has been made.");
                HandleClient clientHandler = new HandleClient(client);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("SERVER, error: " + e.toString());
        }
    }
}
