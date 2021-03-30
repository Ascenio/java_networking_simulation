package dev.ascenio.udp.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
        try (DatagramSocket server = new DatagramSocket(port);) {
            byte[] buffer = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (running.get()) {
                server.receive(packet);
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
                Payload payload = (Payload) objectInputStream.readObject();
                System.out.println("\u001B[0;34m[" + serverID + "]< Received: \u001B[1;34m" + payload);
                if (!payload.wasSentBy(serverID)) {
                    onMessageReceived.accept(payload);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running.set(false);
    }
}
