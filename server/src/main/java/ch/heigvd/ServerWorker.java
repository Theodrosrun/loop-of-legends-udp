package ch.heigvd;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerWorker implements Runnable {
    private DatagramSocket socket;

    private InetAddress clientAddress;

    private int clientPort;

    public ServerWorker() {
        try {
            this.socket = new DatagramSocket(10000);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        System.out.println("ServerWorker");

        while(true){
            System.out.println(receiveMessage());
        }
    }

    private String receiveMessage() {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        try {
            socket.receive(receivePacket);
            // TODO - Avoid constant assignment
            clientAddress = receivePacket.getAddress();
            clientPort = receivePacket.getPort();
        } catch (IOException e) {
            return null;
        }
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }

    private void sendMessage(String message) {
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
