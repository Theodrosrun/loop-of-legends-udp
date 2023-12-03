package ch.heigvd;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int NB_PLAYER = 4;
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private InetSocketAddress multicastGroup;
    private NetworkInterface multicastNetworkInterface;
    private ScheduledExecutorService multicastScheduler;

    private Server(int port, String host) {
        this.multicastScheduler = Executors.newScheduledThreadPool(NB_PLAYER);
        try {
            multicastSocket = new MulticastSocket(port);
            multicastAddress = InetAddress.getByName(host);
            multicastGroup = new InetSocketAddress(multicastAddress, port);
            multicastNetworkInterface = NetworkInterfaceHelper.getFirstNetworkInterfaceAvailable();
            multicastSocket.joinGroup(multicastGroup, multicastNetworkInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Passive discovery protocol pattern
    private void advertisement () {
        try {
        multicastScheduler.scheduleAtFixedRate(() -> {
            try {
                String message = "Hello, from multicast emitter";

                byte[] payload = message.getBytes(StandardCharsets.UTF_8);

                DatagramPacket datagram = new DatagramPacket(
                        payload,
                        payload.length,
                        multicastGroup
                );

                multicastSocket.send(datagram);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 10000, 1000, TimeUnit.MILLISECONDS);

        // Keep the program running for a while
        multicastScheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void start() {
        new Thread(new ServerWorker()).start();
    }

    public static void main(String[] args) {
        String host = "239.1.1.1";
        int port = 20000;

        (new Server(port, host)).start();
    }
}
