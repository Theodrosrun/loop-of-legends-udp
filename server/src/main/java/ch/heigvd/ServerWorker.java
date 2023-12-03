package ch.heigvd;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ServerWorker implements Runnable {

    private DatagramSocket socket;
    private InetAddress clientAddress;
    private int clientPort;

    public ServerWorker(Socket clientSocket, Server server) {

    }

    @Override
    public void run() {
        try {
            String command, response, message, data;
            boolean finished = false;

            while (!finished) {

                switch (Message.fromString(message)) {
                    case INIT:
                        break;

                    case LOBB:

                        break;

                    case JOIN:
                        break;

                    case RADY:
                        break;

                    case DIRE:

                        break;

                    case QUIT:

                        break;

                    case UNKN:

                    default:
                        break;
                }
            }

        } catch (IOException e) {
        }
    }

}