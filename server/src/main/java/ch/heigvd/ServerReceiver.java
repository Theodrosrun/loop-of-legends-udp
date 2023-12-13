package ch.heigvd;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerReceiver implements Runnable {
    // Game configuration
    private Player player;

    // Unicast
    private DatagramSocket unicastSocket;
    private InetAddress unicastClientAddress;
    private int unicastClientPort;
    private ExecutorService executor;
    private Server server;

    ServerReceiver(Server server, DatagramSocket unicastSocket, int nbThreads) {
        this.server = server;
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
            byte[] payload = message.getBytes(StandardCharsets.UTF_8);
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

            // Update at every receive
            unicastClientAddress = packet.getAddress();
            unicastClientPort = packet.getPort();

            return Message.getResponse(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleUnicastMessage(String msg) {
        String message = Message.getMessage(msg);
        String data = Message.getData(msg);

        switch (Message.fromString(message)) {
            case INIT:
                sendUnicast(Message.setCommand(Message.DONE));
                break;

            case LOBB:
                sendUnicast(server.isLobbyFull() ?
                        Message.setCommand(Message.EROR, "The lobby is full") :
                        Message.setCommand(Message.DONE));
                break;

            case JOIN:
                if (server.isLobbyFull()) {
                    sendUnicast(Message.setCommand(Message.EROR, "The lobby is full"));
                    break;
                } else if (server.playerNameAlreadyInUse(data)) {
                    sendUnicast(Message.setCommand(Message.REPT, "Username already used"));
                    break;
                } else if (data.isEmpty()) {
                    sendUnicast(Message.setCommand(Message.REPT, "Username must have minimum 1 character"));

                    break;
                } else {
                    sendUnicast(Message.setCommand(Message.DONE));
                    player = new Player(data);
                    server.joinLobby(player);
                    server.setPlayerReady(player);
                }
                break;

            case RADY:
//                server.setPlayerReady(player);
                break;

            case DIRE:
                Key key = Key.valueOf(data);
                server.setDirection(key, player);
                break;

            case QUIT:
                sendUnicast(Message.setCommand(Message.QUIT, "You left the game"));
                 server.removePlayerFromLobby(player);
                break;

            case UNKN:

            default:
                System.out.println(message);
                break;
        }
    }
}
