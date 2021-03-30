package dev.ascenio;

import java.net.InetAddress;

public class Host {
    private final InetAddress address;
    private final int port;

    public Host(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
