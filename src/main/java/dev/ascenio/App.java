package dev.ascenio;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

import dev.ascenio.client.Client;
import dev.ascenio.client.Host;
import dev.ascenio.server.Server;

public final class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        Env env = new Env("host1.properties");
        setupServer(env.readServerPort());
        Client client = setupClient(env.readHosts());
        sendMessagesPeriodically(client, env.readServerID());
    }

    private static Client setupClient(List<Host> hosts) throws UnknownHostException {
        Client client = new Client(hosts);
        try {
            // Espera os servidores subirem
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(client).start();
        return client;
    }

    private static void setupServer(int port) {
        Server server = new Server(port);
        new Thread(server).start();
    }

    private static void sendMessagesPeriodically(Client client, int serverID) throws InterruptedException {
        Random random = new Random();
        while (true) {
            Thread.sleep(random.nextInt(3000));
            client.send(String.valueOf(serverID));
        }
    }
}
