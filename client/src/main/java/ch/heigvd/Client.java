package ch.heigvd;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Client {
    // Game
    private final Terminal terminal = new Terminal();

    // Unicast
    private DatagramSocket unicastSocket;
    private InetAddress unicastServerAddress;
    private int unicastServerPort;
    private ScheduledExecutorService unicastScheduledExecutorService;
    private String command = "", response = "", message = "", data = "";

    // Multicast
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private InetSocketAddress multicastGroup;
    private NetworkInterface multicastNetworkInterface;
    private ScheduledExecutorService multicastScheduledExecutorService;

    Client(String unicastServerAddress, int unicastserverPort, String multicastHost, int multicastPort) {
        try {
            // Unicast
            this.unicastSocket = new DatagramSocket();
            this.unicastServerAddress = InetAddress.getByName(unicastServerAddress);
            this.unicastServerPort = unicastserverPort;
            this.unicastScheduledExecutorService = Executors.newScheduledThreadPool(1);

            // Multicast
            this.multicastSocket = new MulticastSocket(multicastPort);
            this.multicastAddress = InetAddress.getByName(multicastHost);
            this.multicastGroup = new InetSocketAddress(multicastAddress, multicastPort);
            this.multicastNetworkInterface = NetworkInterfaceHelper.getFirstNetworkInterfaceAvailable();
            this.multicastSocket.joinGroup(multicastGroup, multicastNetworkInterface);
            this.multicastScheduledExecutorService = Executors.newScheduledThreadPool(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() {
        while(true){
            initConnection();
        }
    }

    private void sendUnicast(String message) {
        try {
            byte[] payload = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket commandPacket = new DatagramPacket(
                    payload,
                    payload.length,
                    unicastServerAddress,
                    unicastServerPort);
            unicastSocket.send(commandPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String receiveUnicast() {
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket packet = new DatagramPacket(
                    receiveData,
                    receiveData.length);
            unicastSocket.receive(packet);
            return Message.getResponse(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Passive discovery protocol pattern
    private void receiveMulticast() {
        try {
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );
                multicastSocket.receive(packet);
                System.out.println(Message.getMessage(Message.getResponse(new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8))));
            }
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    private void startReceiveMulticast() {
        multicastScheduledExecutorService.execute(this::receiveMulticast);
    }

    private void stopReceiveMulticast() {
        multicastScheduledExecutorService.shutdown();
    }

    private void initConnection() {
        sendUnicast(Message.setCommand(Message.INIT));
        while (!message.equals("DONE")) {
            message = Message.getMessage(receiveUnicast());
        }
    }

    public static void main(String[] args) {
        // Unicast
        String unicastServerAddress = "127.0.0.1";
        int unicastServerPort = 10000;

        // Mutlicast
        String multicastHost = "239.1.1.1";
        int multicastPort = 20000;

        Client client = new Client(unicastServerAddress, unicastServerPort, multicastHost, multicastPort);
        client.startReceiveMulticast();
        client.run();
    }
}
