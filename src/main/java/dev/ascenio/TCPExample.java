package dev.ascenio;

import java.util.Random;

import dev.ascenio.tcp.client.Client;
import dev.ascenio.tcp.server.Server;

public final class TCPExample {
    public static void run() {
        new Thread(() -> host("host1.properties")).start();
        new Thread(() -> host("host2.properties")).start();
        new Thread(() -> host("host3.properties")).start();
        new Thread(() -> host("host4.properties")).start();
    }

    private static void host(String host) {
        Env env = new Env(host);
        int serverID = env.readServerID();
        Client client = new Client(env.readHosts(), serverID);
        Server server = new Server(env.readServerPort(), client::send, serverID);
        startServer(server);
        try {
            startClient(client);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private static void startServer(Server server) {
        new Thread(server).start();
    }

    private static void startClient(Client client) throws InterruptedException {
        try {
            // Awaits for the servers to turn on
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(client).start();
        Random random = new Random();
        while (true) {
            if (random.nextBoolean()) {
                client.send(new Payload("", client.getClientID()));
            } else {
                client.send(new Payload(0, client.getClientID()));
            }
            Thread.sleep(random.nextInt(3000));
        }
    }
}
