package ch.heigvd;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerWorker implements Runnable {
    private DatagramSocket unicastSocket;

    private InetAddress unicastClientAddress;

    private int unicastClientPort;

    public ServerWorker() {
        try {
            this.unicastSocket = new DatagramSocket(10000);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(true){
            System.out.println(receiveUnicast());
        }
    }

    private String receiveUnicast() {
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket packet = new DatagramPacket(
                    receiveData,
                    receiveData.length);
            unicastSocket.receive(packet);
            // TODO - Avoid constant assignment
            unicastClientAddress = packet.getAddress();
            unicastClientPort = packet.getPort();
            return new String(packet.getData(), 0, packet.getLength());
        } catch (IOException e) {
            return null;
        }
    }

    private void sendUnicast(String message) {
        byte[] payload = message.getBytes();
        DatagramPacket datagram = new DatagramPacket(payload, payload.length, unicastClientAddress, unicastClientPort);
        try {
            unicastSocket.send(datagram);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
