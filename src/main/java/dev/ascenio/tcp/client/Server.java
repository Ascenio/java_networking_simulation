package dev.ascenio.tcp.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.ascenio.Payload;

public class Server implements Runnable, Closeable {
    private final Socket socket;
    private final BlockingQueue<Payload> queue;
    private final AtomicBoolean running;

    public Server(Socket socket) {
        this.socket = socket;
        this.queue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(true);
    }

    public void send(Payload payload) {
        queue.add(payload);
    }

    @Override
    public void run() {
        try (ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());) {
            while (running.get()) {
                Payload payload = queue.take();
                stream.writeObject(payload);
                stream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        running.set(false);
        socket.close();
    }
}
