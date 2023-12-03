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
        try {
            // Multicast
            this.multicastSocket = new MulticastSocket(port);
            this.multicastAddress = InetAddress.getByName(host);
            this.multicastGroup = new InetSocketAddress(multicastAddress, port);
            this.multicastNetworkInterface = NetworkInterfaceHelper.getFirstNetworkInterfaceAvailable();
            this.multicastSocket.joinGroup(multicastGroup, multicastNetworkInterface);
            this.multicastScheduler = Executors.newScheduledThreadPool(NB_PLAYER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Passive discovery protocol pattern
    private void emitMulticast() {
        try {
        multicastScheduler.scheduleAtFixedRate(() -> {
            try {
                String message = "Server is emitting";

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
        String multicastHost = "239.1.1.1";
        int multicastPort = 20000;

        Server server = new Server(multicastPort, multicastHost);
        server.emitMulticast();
        server.start();
    }
}
