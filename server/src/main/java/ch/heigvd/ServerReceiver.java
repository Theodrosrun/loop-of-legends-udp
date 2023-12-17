package ch.heigvd;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

public class ServerReceiver implements Runnable {
    private Server server;

    // Unicast
    private DatagramSocket unicastSocket;
    private InetAddress unicastClientAddress;
    private int unicastClientPort;
    private ExecutorService executor;

    /**
     * Constructor for ServerReceiver. Initializes the unicast socket and sets up the thread pool for handling messages.
     *
     * @param server         The server instance.
     * @param unicastSocket  The unicast DatagramSocket for communication.
     * @param nbThreads      Number of threads in the thread pool.
     */
    ServerReceiver(Server server, DatagramSocket unicastSocket, int nbThreads) {
        this.server = server;
        this.unicastSocket = unicastSocket;
        this.unicastClientAddress = unicastSocket.getInetAddress();
        this.unicastClientPort = unicastSocket.getPort();
        this.executor = Executors.newFixedThreadPool(nbThreads);
    }

    /**
     * The main run method for the thread. Continuously receives and handles unicast messages.
     */
    @Override
    public void run() {
        while (true) {
            String message = receiveUnicast();
            if (message != null) {
                executor.submit(() -> handleUnicastMessage(message));
            }
        }
    }

    /**
     * Sends a unicast message to the client.
     *
     * @param message The message to be sent.
     */
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

    /**
     * Receives a unicast message from the client.
     *
     * @return The received message as a String.
     */
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

    /**
     * Handles a received unicast message based on its content.
     *
     * @param msg The received message.
     */
    private void handleUnicastMessage(String msg) {
        String message = Message.getMessage(msg);
        String data = Message.getData(msg);
        UUID senderUuid = Message.getUUID(msg);
        Player p = server.getPlayerByUUID(senderUuid);

        switch (Message.fromString(message)) {
            case INIT:
                sendUnicast(Message.setCommand(server.getUuid(), Message.DONE));
                break;

            case LOBB:
                sendUnicast(server.isLobbyFull() ?
                        Message.setCommand(server.getUuid(), Message.EROR, "The lobby is full") :
                        Message.setCommand(server.getUuid(), Message.DONE));
                break;

            case JOIN:
                if (server.isLobbyFull()) {
                    sendUnicast(Message.setCommand(server.getUuid(), Message.EROR, "The lobby is full"));
                    break;
                } else if (server.playerNameAlreadyInUse(data)) {
                    sendUnicast(Message.setCommand(server.getUuid(), Message.REPT, "Username already used"));
                    break;
                } else if (data.isEmpty()) {
                    sendUnicast(Message.setCommand(server.getUuid(), Message.REPT, "Username must have minimum 1 character"));

                    break;
                } else {
                    sendUnicast(Message.setCommand(server.getUuid(), Message.DONE));
                    server.joinLobby(new Player(data, senderUuid));
                }
                break;

            case RADY:
                if (p == null) {
                    sendUnicast(Message.setCommand(server.getUuid(), Message.EROR, "You are not in the lobby"));
                }
                server.setPlayerReady(p);
                break;

            case DIRE:
                if (p == null) {
                    sendUnicast(Message.setCommand(server.getUuid(), Message.EROR, "You are not in the game"));
                }
                Key key = Key.valueOf(data);
                server.setDirection(key, p);
                break;

            case QUIT:
                sendUnicast(Message.setCommand(server.getUuid(), Message.QUIT, "You left the game"));
                server.removePlayerFromLobby(p);
                break;

            case UNKN:

            default:
                break;
        }
    }
}
