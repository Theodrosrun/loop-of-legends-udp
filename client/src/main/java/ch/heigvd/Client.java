package ch.heigvd;

import java.net.*;

public class Client {
    private DatagramSocket socket;

    private InetAddress serverAddress;

    private int serverPort;

    public Client(InetAddress serverAdress, int serverPort) {
        try {
            this.socket = new DatagramSocket();
            this.serverAddress = serverAdress;
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
        // Validate arguments
        if (args.length == 0) {
            args = new String[]{"127.0.0.1", "20000"};
        }
        else if (args.length != 2) {
            System.err.println("Usage: client <address> <port>");
            return;
        }

        InetAddress serverAdress;
        int serverPort;

        // Resolve the address
        try {
            serverAdress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException ex) {
            System.err.println("Error: The address " + args[0] + " is unknown.");
            return;
        }

        // Validate the port number
        try {
            serverPort = Integer.parseInt(args[1]);
            if (serverPort < 0 || serverPort > 65535) {
                System.err.println("Error: Port number must be between 0 and 65535.");
                return;
            }
        } catch (NumberFormatException ex) {
            System.err.println("Error: Port number must be an integer.");
            return;
        }

        // Create the client
        try {
            Client client = new Client(serverAdress, serverPort);
        } catch (Exception ex) {
            System.err.println("Error creating the client: " + ex.getMessage());
        }
    }
}
