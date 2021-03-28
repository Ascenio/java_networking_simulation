package dev.ascenio.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MessageConsumer implements Consumer<String>, Runnable {
    private final Set<Client> clients;
    private final BlockingQueue<String> queue;
    private final AtomicBoolean running;

    public MessageConsumer() {
        queue = new LinkedBlockingQueue<>();
        running = new AtomicBoolean(true);
        clients = Collections.synchronizedSet(new HashSet<>());
    }

    public void add(Client client) {
        clients.add(client);
    }

    @Override
    public void accept(String message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                String message = queue.take();
                System.out.println("\u001B[0;34m< Received: \u001B[1;34m" + message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running.set(false);
    }
}
