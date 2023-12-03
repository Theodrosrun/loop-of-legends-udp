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
            System.out.println(receiveMessage());
        }
    }

    private String receiveMessage() {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        try {
            unicastSocket.receive(receivePacket);
            // TODO - Avoid constant assignment
            unicastClientAddress = receivePacket.getAddress();
            unicastClientPort = receivePacket.getPort();
        } catch (IOException e) {
            return null;
        }
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }

    private void sendMessage(String message) {
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, unicastClientAddress, unicastClientPort);
        try {
            unicastSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
