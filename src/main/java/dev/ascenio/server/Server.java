package dev.ascenio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server implements Runnable {
    private final MessageConsumer messageConsumer;
    private final int port;
    private final AtomicBoolean running;

    public Server(int port) {
        this.messageConsumer = new MessageConsumer();
        new Thread(messageConsumer).start();
        this.port = port;
        this.running = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        while (running.get()) {
            try (ServerSocket server = new ServerSocket(port);) {
                System.out.println("\u001B[0;33mWaiting for connections..");
                Socket socket = server.accept();
                System.out.println("Accepted: " + socket.getInetAddress());
                Client client = new Client(socket, messageConsumer);
                new Thread(client).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running.set(false);
        messageConsumer.stop();
    }
}
