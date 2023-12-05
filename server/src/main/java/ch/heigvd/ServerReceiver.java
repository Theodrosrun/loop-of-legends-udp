package ch.heigvd;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerReceiver implements Runnable {
    private DatagramSocket unicastSocket;
    private InetAddress unicastClientAddress;
    private int unicastClientPort;
    private ExecutorService executor;

    ServerReceiver(DatagramSocket unicastSocket, int nbThreads) {
        this.unicastSocket = unicastSocket;
        this.unicastClientAddress = unicastSocket.getInetAddress();
        this.unicastClientPort = unicastSocket.getPort();
        this.executor = Executors.newFixedThreadPool(nbThreads);
    }

    @Override
    public void run() {
        while(true){
            String message = receiveUnicast();
            if (message != null) {
                executor.submit(() -> handleUnicastMessage(message));
            }
        }
    }

    private void sendUnicast(String message) {
        try {
            byte[] payload = message.getBytes();
            DatagramPacket datagram = new DatagramPacket(
                    payload,
                    payload.length,
                    unicastClientAddress,
                    unicastClientPort);
            unicastSocket.send(datagram);
        } catch (IOException e) {
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
            return new String(packet.getData(), 0, packet.getLength());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleUnicastMessage(String message) {
        System.out.println("Traitement du message: " + message);
    }
}
