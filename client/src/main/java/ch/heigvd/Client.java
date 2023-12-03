package ch.heigvd;

import java.net.*;

public class Client {
    private DatagramSocket socket;

    private InetAddress serverAddress;

    private int serverPort;

    public Client(String serverAddress, int serverPort) {
        try {
            this.socket = new DatagramSocket();
            this.serverAddress = InetAddress.getByName(serverAddress);
            this.serverPort = serverPort;
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

    public void receiveMessage() {
        try {
            byte[] response = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(
                    response,
                    response.length);
            socket.receive(responsePacket);
            String message = new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void test() {
        sendMessage("Test");
    }

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int serverPort = 20000;

        Client client = new Client(serverAddress, serverPort);
        client.test();
    }
}

