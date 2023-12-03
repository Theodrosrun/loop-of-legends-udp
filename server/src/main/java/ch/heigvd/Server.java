package ch.heigvd;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int NB_PLAYER = 4;

    // Unicast
    private DatagramSocket unicastSocket;
    private ExecutorService unicastExecutorService;

    // Mulitcast
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private InetSocketAddress multicastGroup;
    private NetworkInterface multicastNetworkInterface;
    private ScheduledExecutorService multicastScheduler;

    private Server(int unicastPort, int multicastPort, String multicastHost) {
        try {
            // Unicast
            this.unicastSocket = new DatagramSocket(unicastPort);
            this.unicastExecutorService = Executors.newFixedThreadPool(NB_PLAYER);

            // Multicast
            this.multicastSocket = new MulticastSocket(multicastPort);
            this.multicastAddress = InetAddress.getByName(multicastHost);
            this.multicastGroup = new InetSocketAddress(multicastAddress, multicastPort);
            this.multicastNetworkInterface = NetworkInterfaceHelper.getFirstNetworkInterfaceAvailable();
            this.multicastSocket.joinGroup(multicastGroup, multicastNetworkInterface);
            this.multicastScheduler = Executors.newScheduledThreadPool(NB_PLAYER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveUnicast() {
        while (true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length);
                unicastSocket.receive(packet);

                unicastExecutorService.submit(new ServerWorker(unicastSocket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopReceiveUnicast() {
        unicastExecutorService.shutdown();
    }

    // Passive discovery protocol pattern
    private void sendMulticast() {
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
        }, 1000, 1000, TimeUnit.MILLISECONDS);

        // Keep the program running for a while
        multicastScheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // Unicast
        int unicastPort = 10000;

        // Multicast
        String multicastHost = "239.1.1.1";
        int multicastPort = 20000;

        Server server = new Server(unicastPort, multicastPort, multicastHost);
        server.sendMulticast();
        server.receiveUnicast();
    }
}
