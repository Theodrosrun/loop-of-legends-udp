package ch.heigvd;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {
    private DatagramSocket socket;

    private InetAddress serverAddress;

    private int serverPort;

    ScheduledExecutorService scheduler;

    public Client(String serverAddress, int serverPort) {
        try {
            this.socket = new DatagramSocket();
            this.serverAddress = InetAddress.getByName(serverAddress);
            this.serverPort = serverPort;
            this.scheduler = Executors.newScheduledThreadPool(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            byte[] command = message.getBytes();
            DatagramPacket commandPacket = new DatagramPacket(
                    command,
                    command.length,
                    serverAddress,
                    serverPort);
            socket.send(commandPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        try {
            byte[] response = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(
                    response,
                    response.length);
            socket.receive(responsePacket);
            return new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void test() {
        try{
            scheduler.scheduleAtFixedRate(() -> {
                sendMessage("I want to play bitch!");
            }, 10000, 1000, TimeUnit.MILLISECONDS);

            // Keep the program running for a while
            scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(receiveMessage());
    }

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int serverPort = 10000;

        Client client = new Client(serverAddress, serverPort);
        client.test();
    }
}

