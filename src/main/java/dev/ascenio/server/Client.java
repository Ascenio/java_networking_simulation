package dev.ascenio.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.function.Consumer;

import dev.ascenio.Payload;

public class Client implements Runnable {
    private final Socket socket;
    private final Consumer<Payload> consumer;

    public Client(Socket socket, Consumer<Payload> consumer) {
        this.socket = socket;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try (ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()))) {
            while (true) {
                try {
                    Payload payload = (Payload) reader.readObject();
                    consumer.accept(payload);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
