package ch.heigvd;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * The class that represent the server worker
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());

    /**
     * the player that the server worker is associated with
     */
    private Player player;

    /**
     * the server that the server worker work for
     */
    private Server server;

    /**
     * the socket used to communicate with the client
     */
    private Socket clientSocket;

    /**
     * the input stream used to communicate with the client
     */
    private BufferedReader clientInput = null;

    /**
     * the output stream used to communicate with the client
     */
    private BufferedWriter serverOutput = null;

    /**
     * the thread that update the gui of the client
     */
    private Thread thGuiUpdate = new Thread(this::guiUpdate);

    /**
     * The constructor
     *
     * @param clientSocket The socket used to communicate with the client
     * @param server       The server that the server worker work for
     */
    public ServerWorker(Socket clientSocket, Server server) {
        this.server = server;
        this.clientSocket = clientSocket;

        try {
            clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            serverOutput = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Server exception on constructor: " + e);

            try {
                if (clientInput != null) clientInput.close();
                if (serverOutput != null) serverOutput.close();
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException e2) {
                System.err.println("Error closing resources: " + e2);
            }
        }

        Thread exitTh = new Thread(new Exit(clientSocket, serverOutput, clientInput));
        Runtime.getRuntime().addShutdownHook(exitTh);
    }

    /**
     * The run method of the thread
     */
    @Override
    public void run() {
        try {
            String command, response, message, data;
            boolean finished = false;

            while (!finished) {
                response = Message.getResponse(clientInput);
                if (response == null) break;
                message = Message.getMessage(response);
                data = Message.getData(response);

                switch (Message.fromString(message)) {
                    case INIT:
                        command = Message.setCommand(Message.DONE);
                        serverOutput.write(command);
                        serverOutput.flush();
                        break;

                    case LOBB:
                        command = server.isFull() ?
                                Message.setCommand(Message.EROR, "The lobby is full") :
                                Message.setCommand(Message.DONE);
                        serverOutput.write(command);
                        serverOutput.flush();
                        break;

                    case JOIN:
                        if (server.isFull()) {
                            serverOutput.write(Message.setCommand(Message.EROR, "The lobby is full"));
                            serverOutput.flush();
                            break;
                        } else if (server.playerNameAlreadyInUse(data)) {
                            serverOutput.write(Message.setCommand(Message.REPT, "Username already used"));
                            serverOutput.flush();
                            break;
                        } else if (data.isEmpty()) {
                            serverOutput.write(Message.setCommand(Message.REPT, "Username must have minimum 1 character"));
                            serverOutput.flush();
                            break;
                        } else {
                            serverOutput.write(Message.setCommand(Message.DONE));
                            serverOutput.flush();
                            player = new Player(data);
                            server.joinLobby(player);
                            thGuiUpdate.start();
                        }
                        break;

                    case RADY:
                        server.setReady(player);
                        break;

                    case DIRE:
                        KEY key = KEY.valueOf(data);
                        server.setDirection(key, player);
                        break;

                    case QUIT:
                        serverOutput.write(Message.setCommand(Message.QUIT, "You left the game"));
                        serverOutput.flush();
                        if (player != null) server.removePlayer(player);
                        finished = true;
                        break;

                    case UNKN:

                    default:
                        break;
                }
            }

            LOG.info("A client left the game");

            serverOutput.close();
            clientInput.close();
            clientSocket.close();


        } catch (IOException e) {
            System.err.println("Server exception: " + e);

            try {
                if (clientInput != null) clientInput.close();
                if (serverOutput != null) serverOutput.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e2) {
                System.err.println("Error closing resources: " + e2);
            }
        }
    }

    /**
     * Update the gui of the client
     */
    public void guiUpdate() {
        while (true) {
            try {
                Thread.sleep(server.getGameFrequency() / 2);
            } catch (InterruptedException e) {
                System.err.println("Server exception on gui: " + e);
            }

            StringBuilder sb = new StringBuilder( server.getBoard().toString());
            sb.append("\n");
            sb.append(server.getInfos());

            String command = Message.setCommand(Message.UPTE, sb.toString());

            if (!clientSocket.isClosed()) {
                try {
                    serverOutput.write(command);
                    serverOutput.flush();
                } catch (IOException e) {
                    System.err.println("Server exception on gui if not close : " + e);

                    try {
                        if (serverOutput != null) serverOutput.close();
                        if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
                    } catch (IOException e2) {
                        System.err.println("Error closing resources: " + e2);
                    }
                }
            } else {
                break;
            }
        }
    }
}