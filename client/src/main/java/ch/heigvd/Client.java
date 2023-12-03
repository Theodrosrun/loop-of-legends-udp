package ch.heigvd;

import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {
    private DatagramSocket unicastSocket;

    private InetAddress unicastServerAddress;

    private int unicastServerPort;

    ScheduledExecutorService unicastScheduler;

    public Client(String unicastServerAddress, int unicastserverPort) {
        try {
            this.unicastSocket = new DatagramSocket();
            this.unicastServerAddress = InetAddress.getByName(unicastServerAddress);
            this.unicastServerPort = unicastserverPort;
            this.unicastScheduler = Executors.newScheduledThreadPool(1);
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
                    unicastServerAddress,
                    unicastServerPort);
            unicastSocket.send(commandPacket);
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
            unicastSocket.receive(responsePacket);
            return new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void test() {
        try{
            unicastScheduler.scheduleAtFixedRate(() -> {
                sendMessage("I want to play bitch!");
            }, 10000, 1000, TimeUnit.MILLISECONDS);

            // Keep the program running for a while
            unicastScheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(receiveMessage());
    }

    public static void main(String[] args) {
        String unicastServerAddress = "127.0.0.1";
        int unicastServerPort = 10000;

        Client client = new Client(unicastServerAddress, unicastServerPort);
        client.test();
    }
}

