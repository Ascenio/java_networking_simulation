package dev.ascenio.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.ascenio.Payload;

public class Client implements Runnable, Closeable {
    private final List<Host> hosts;
    private final List<Server> servers;
    private final BlockingQueue<Payload> queue;
    private final AtomicBoolean running;
    private final int clientID;

    public Client(List<Host> hosts, int clientID) {
        this.hosts = hosts;
        this.servers = new ArrayList<>();
        this.queue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(true);
        this.clientID = clientID;
    }

    public int getClientID() {
        return clientID;
    }

    public void send(Payload payload) {
        queue.add(payload);
    }

    @Override
    public void run() {
        for (Host host : hosts) {
            try {
                Socket socket = new Socket(host.getAddress(), host.getPort());
                Server server = new Server(socket);
                new Thread(server).start();
                servers.add(server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (running.get()) {
            try {
                Payload payload = queue.take();
                if (payload.isInteger()) {
                    payload.append(clientID);
                } else {
                    payload.append(String.valueOf(clientID));
                }
                System.out.println("\u001B[0;31m[" + clientID + "]> Sending \u001B[1;31m" + payload);
                for (Server server : servers) {
                    server.send(payload);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        running.set(false);
        for (Server server : servers) {
            server.close();
        }
    }
}
