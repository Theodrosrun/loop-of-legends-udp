package ch.heigvd;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ServerReceiver implements Runnable {
    private DatagramSocket unicastSocket;
    private InetAddress unicastClientAddress;
    private int unicastClientPort;
    private Server server;
    final ArrayList<Thread> threads = new ArrayList<>();

    private Semaphore semaphore = new Semaphore(1);

    ServerReceiver(DatagramSocket unicastSocket, Server server, int nbThreads) {
        initThreads(nbThreads);
        this.server = server;
        this.unicastSocket = unicastSocket;
        this.unicastClientAddress = unicastSocket.getInetAddress();
        this.unicastClientPort = unicastSocket.getPort();
    }

    /**
     * Initialize the threads
     * @param nbThreads number of threads to initialize
     */
    private void initThreads(int nbThreads) {
        for (int i = 0; i < nbThreads; i++) {
            threads.add(new Thread(this));
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(receiveUnicast());
            semaphore.release();

            /**
             * Switch user
             * case 1
             *  fifo1
             * case 2
             *  fifo2
             * case 3
             *  fifo3
             * case 4
             *  fifo4
             */

            //Traiter l'information
        }
    }

    private void sendUnicast(String message) {
        byte[] payload = message.getBytes();
        DatagramPacket datagram = new DatagramPacket(
                payload,
                payload.length,
                unicastClientAddress,
                unicastClientPort);
        try {
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
            return null;
        }
    }
}
