package dev.ascenio;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Env {
    private final String resource;
    private final Properties properties;

    public Env(String resource) {
        this.resource = resource;
        this.properties = readProperties();
    }

    public List<Host> readHosts() {
        String[] hostsStrings = properties.getProperty("servers").split(",");
        List<Host> hosts = Arrays.stream(hostsStrings).map(hostWithPort -> {
            String[] data = hostWithPort.split(":");
            String host = data[0];
            int port = Integer.valueOf(data[1]);

            try {
                return new Host(InetAddress.getByName(host), port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            }
            return null;
        }).collect(Collectors.toList());
        return hosts;
    }

    public int readServerPort() {
        int port = Integer.valueOf(properties.getProperty("server_port"));
        return port;
    }

    public int readServerID() {
        int port = Integer.valueOf(properties.getProperty("server_id"));
        return port;
    }

    private Properties readProperties() {
        InputStream resourceStream = App.class.getClassLoader().getResourceAsStream(resource);
        if (resourceStream == null) {
            System.out.println("❌ File '" + resource + "' doesn't exist");
            System.exit(1);
        }
        Properties properties = new Properties();
        try {
            properties.load(resourceStream);
        } catch (IOException e) {
            System.out.println("😢 Error while loading '" + resource + "'");
            System.exit(1);
        }
        System.out.println("✔️ File '" + resource + "' loaded");
        return properties;
    }

}
