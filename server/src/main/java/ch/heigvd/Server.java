package ch.heigvd;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Server {
    private static final int NB_PLAYER = 4;
    private MulticastSocket socket;

    private ScheduledExecutorService scheduler;

    private Server(int port, String host) {
        this.scheduler = Executors.newScheduledThreadPool(NB_PLAYER);
        try {
            socket = new MulticastSocket(port);
            InetAddress multicastAddress = InetAddress.getByName(host);
            InetSocketAddress group = new InetSocketAddress(multicastAddress, port);
            NetworkInterface networkInterface = NetworkInterfaceHelper.getFirstNetworkInterfaceAvailable();
            socket.joinGroup(group, networkInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void start() {
        System.out.println("Server");
        new Thread(new ServerWorker()).start();
    }

    public static void main(String[] args) {
        String host = "239.1.1.1";
        int port = 20000;

        (new Server(port, host)).start();
    }
}
