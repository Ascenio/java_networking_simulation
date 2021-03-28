package dev.ascenio.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Consumer;

public class Client implements Runnable {
    private final Socket socket;
    private final Consumer<String> consumer;

    public Client(Socket socket, Consumer<String> consumer) {
        this.socket = socket;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            reader.lines().forEach(consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
