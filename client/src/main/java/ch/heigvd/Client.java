package ch.heigvd;

import com.googlecode.lanterna.input.KeyStroke;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.lang.System.*;

public class Client {
    // Game
    protected final Terminal terminal = new Terminal();
    protected final InputHandler inputHandler = new InputHandler(terminal, 50);
    private final UUID uuid = UUID.randomUUID();

    private final int DISPLAY_FREQUENCY = 25;
    private UUID serverUUID = null;
    private String board = "";
    // Unicast
    private DatagramSocket unicastSocket;
    private InetAddress unicastServerAddress;
    private int unicastServerPort;
    private ScheduledExecutorService unicastScheduledExecutorService;
    private String command = "", response = "", message = "", data = "";

    // Multicast
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private InetSocketAddress multicastGroup;
    private NetworkInterface multicastNetworkInterface;
    private ScheduledExecutorService multicastScheduledExecutorService;

    Client(String unicastServerAddress, int unicastserverPort, String multicastHost, int multicastPort) {
        try {
            // Unicast
            this.unicastSocket = new DatagramSocket();
            this.unicastServerAddress = InetAddress.getByName(unicastServerAddress);
            this.unicastServerPort = unicastserverPort;
            this.unicastScheduledExecutorService = Executors.newScheduledThreadPool(1);

            // Multicast
            this.multicastSocket = new MulticastSocket(multicastPort);
            this.multicastAddress = InetAddress.getByName(multicastHost);
            this.multicastGroup = new InetSocketAddress(multicastAddress, multicastPort);
            NetworkInterfaceSelector selector = new NetworkInterfaceSelector();
            this.multicastNetworkInterface = selector.selectNetworkInterface();
            this.multicastSocket.joinGroup(multicastGroup, multicastNetworkInterface);
            this.multicastScheduledExecutorService = Executors.newScheduledThreadPool(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendUnicast(String message) {
        try {
            byte[] payload = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket commandPacket = new DatagramPacket(
                    payload,
                    payload.length,
                    unicastServerAddress,
                    unicastServerPort);
            unicastSocket.send(commandPacket);
        } catch (Exception e) {
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
            String reponse =  Message.getResponse(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
            if (serverUUID == null || Message.getUUID(reponse).equals(serverUUID)) return reponse;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void receiveMulticast() {
        try {
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );
                multicastSocket.receive(packet);
                board = Message.getData(Message.getResponse(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8)));
                Thread.sleep(DISPLAY_FREQUENCY);
            }
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    protected void startReceiveMulticast() {
        multicastScheduledExecutorService.execute(this::receiveMulticast);
    }

    private void stopReceiveMulticast() {
        multicastScheduledExecutorService.shutdown();
    }

    private void initConnection() {
        sendUnicast(Message.setCommand(uuid, Message.INIT));
        while (!message.equals("DONE")) {
            response = receiveUnicast();
            if (response == null) continue;
            serverUUID = Message.getUUID(response);
            message = Message.getMessage(response);
        }
    }

    private void tryLobby() {
        sendUnicast(Message.setCommand(uuid, Message.LOBB));
        response = receiveUnicast();
        message = Message.getMessage(response);
        data = Message.getData(response);
        messageHandling(message, data);
    }

    private void join() {
        terminal.clear();
        terminal.print(Intro.logo);

        while (inputHandler.getKey() != Key.ENTER) {
            if (inputHandler.getKey() == Key.QUIT) {
                quit();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        inputHandler.pauseHandler();

        while (true) {
            String username = terminal.userInput();

            sendUnicast(Message.setCommand(uuid, Message.JOIN, username));

            response = receiveUnicast();
            message = Message.getMessage(response);
            data = Message.getData(response);
            messageHandling(message, data);

            if (message.equals("DONE")) {
                break;
            }

            inputHandler.restoreHandler();
            inputHandler.resetKey();

            while (inputHandler.getKey() != Key.ENTER) {
                terminal.print(data + "\n" + "Press enter to continue\n");
                try {
                    Thread.sleep(DISPLAY_FREQUENCY);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            inputHandler.pauseHandler();

        }

        inputHandler.restoreHandler();
    }

    private void waitReady() {
        inputHandler.resetKey();
        boolean isReady = false;

        while (!isReady) {
            if (inputHandler.getKey() == Key.READY) {
                sendUnicast(Message.setCommand(uuid ,Message.RADY));
                inputHandler.resetKey();
                isReady = true;
            }
            else if (inputHandler.getKey() == Key.HELP) {
                terminal.clear();
                terminal.print(Help.Rules + "\n" + Help.Commands);
                requestKey(Key.HELP);
            }
            if (inputHandler.getKey() == Key.QUIT) {
                quit();
            }

            try {
                Thread.sleep(DISPLAY_FREQUENCY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            printBoard();
        }
    }

    private void controlSnake() {
        while (inputHandler.getKey() != Key.QUIT) {
            KeyStroke key = inputHandler.getKeyStroke();
            if (InputHandler.isDirection(key)) {
                sendUnicast(Message.setCommand(uuid, Message.DIRE, Key.parseKeyStroke(key).toString()));
                inputHandler.resetKey();
            }

            try {
                Thread.sleep(DISPLAY_FREQUENCY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            printBoard();
        }
        quit();
    }

    public void printBoard() {
        terminal.clear();
        terminal.print(board);
    }

    protected void quit() {
        stopReceiveMulticast();
        sendUnicast(Message.setCommand(uuid, Message.QUIT));
        response = receiveUnicast();
        message = Message.getMessage(response);
        data = Message.getData(response);

        if (!message.equals("QUIT")) {
            terminal.print("Error :" + data);
            exit(1);
        }

        terminal.clear();
        terminal.print(data + "\n" + "Press enter to exit\n");

        while (inputHandler.getKey() != Key.ENTER) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        exit(0);
    }

    private void messageHandling(String message, String data) {
        switch (message) {
            case "EROR":
                terminal.clear();
                terminal.print("Error :" + data + "\n" + "Press enter to exit\n");
                requestKey(Key.ENTER);
                exit(0);
                break;
            case "QUIT":
                terminal.clear();
                terminal.print("Server left \n" + data + "\n" + "Press enter to exit\n");
                requestKey(Key.ENTER);
                exit(0);
            default:
                break;
        }
    }

    private void requestKey(Key key){
        inputHandler.resetKey();
        while (inputHandler.getKey() != key) {
            inputHandler.restoreHandler();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        inputHandler.resetKey();
    }

    public static void main(String[] args) {
        // Unicast
        String unicastServerAddress = "127.0.0.1";
        int unicastServerPort = 10000;

        // Mutlicast
        String multicastHost = "239.1.1.1";
        int multicastPort = 20000;

        Client client = new Client(unicastServerAddress, unicastServerPort, multicastHost, multicastPort);

        client.startReceiveMulticast();
        client.initConnection();
        client.tryLobby();
        client.join();
        client.waitReady();
        client.controlSnake();
    }
}
