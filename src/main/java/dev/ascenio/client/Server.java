package dev.ascenio.client;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server implements Runnable, Closeable {
    private final Socket socket;
    private final BlockingQueue<String> queue;
    private final AtomicBoolean running;

    public Server(Socket socket) {
        this.socket = socket;
        this.queue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(true);
    }

    public void send(String message) {
        queue.add(message);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String message = queue.take();
                writer.write(message + "\n");
                writer.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        running.set(false);
        socket.close();
    }
}
