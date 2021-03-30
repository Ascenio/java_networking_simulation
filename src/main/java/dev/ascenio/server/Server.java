package dev.ascenio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import dev.ascenio.Payload;

public class Server implements Runnable {
    private final int port;
    private final AtomicBoolean running;
    private final Consumer<Payload> onMessageReceived;
    private final int serverID;

    public Server(int port, Consumer<Payload> onMessageReceived, int serverID) {
        this.port = port;
        this.running = new AtomicBoolean(true);
        this.onMessageReceived = onMessageReceived;
        this.serverID = serverID;
    }

    @Override
    public void run() {
        while (running.get()) {
            try (ServerSocket server = new ServerSocket(port);) {
                System.out.println("\u001B[0;33mWaiting for connections..");
                Socket socket = server.accept();
                System.out.println("Accepted: " + socket.getInetAddress());
                Client client = new Client(socket, (payload) -> {
                    System.out.println("\u001B[0;34m[" + serverID + "]< Received: \u001B[1;34m" + payload);
                    if (!payload.wasSentBy(serverID)) {
                        onMessageReceived.accept(payload);
                    }
                });
                new Thread(client).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running.set(false);
    }
}
