package ch.heigvd;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    // Game configuration
    private static final int NB_PLAYERS = 4;
    private static final long INIT_DELAY = 1000;
    private static final int PERIOD = 1000;

    // Unicast
    private static final int NB_THREADS = 1;
    private DatagramSocket unicastSocket;
    private ExecutorService unicastExecutorService;

    // Mulitcast
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private InetSocketAddress multicastGroup;
    private NetworkInterface multicastNetworkInterface;
    private ScheduledExecutorService multicastScheduledExecutorService;

    Server(int unicastPort, int multicastPort, String multicastHost) {
        try {
            // Unicast
            this.unicastSocket = new DatagramSocket(unicastPort);
            this.unicastExecutorService = Executors.newFixedThreadPool(NB_THREADS);

            // Multicast
            this.multicastSocket = new MulticastSocket(multicastPort);
            this.multicastAddress = InetAddress.getByName(multicastHost);
            this.multicastGroup = new InetSocketAddress(multicastAddress, multicastPort);
            this.multicastNetworkInterface = NetworkInterfaceHelper.getFirstNetworkInterfaceAvailable();
            this.multicastSocket.joinGroup(multicastGroup, multicastNetworkInterface);
            this.multicastScheduledExecutorService = Executors.newScheduledThreadPool(NB_THREADS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void acceptClient() {
        while (true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length);
                unicastSocket.receive(packet);

                unicastExecutorService.submit(new ServerReceiver(unicastSocket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopAcceptClient() {
        unicastExecutorService.shutdown();
    }

    // Passive discovery protocol pattern
    private void sendMulticast() {
        multicastScheduledExecutorService.scheduleAtFixedRate(() -> {
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
        }, INIT_DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }

    private void stopSendMulticast() {
        multicastScheduledExecutorService.shutdown();
    }

    public static void main(String[] args) {
        // Unicast
        int unicastPort = 10000;

        // Multicast
        String multicastHost = "239.1.1.1";
        int multicastPort = 20000;

        Server server = new Server(unicastPort, multicastPort, multicastHost);
        server.sendMulticast();
        server.acceptClient();
    }
}
