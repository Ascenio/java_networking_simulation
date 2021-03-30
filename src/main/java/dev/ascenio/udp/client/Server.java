package dev.ascenio.udp.client;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.ascenio.Host;
import dev.ascenio.Payload;

public class Server implements Runnable, Closeable {
    private final Host host;
    private final BlockingQueue<Payload> queue;
    private final AtomicBoolean running;

    public Server(Host host) {
        this.host = host;
        this.queue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(true);
    }

    public void send(Payload payload) {
        queue.add(payload);
    }

    @Override
    public void run() {
        try (DatagramSocket server = new DatagramSocket();) {
            server.setSoTimeout(3000);
            while (running.get()) {
                Payload payload = queue.take();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
                outputStream.writeObject(payload);
                outputStream.flush();
                byte[] buffer = byteArrayOutputStream.toByteArray();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host.getAddress(), host.getPort());
                server.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        running.set(false);
    }
}
