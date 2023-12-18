package ch.heigvd;

import static java.lang.System.exit;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "Viewer", mixinStandardHelpOptions = true, version = "Viewer 1.0",
        description = "Starts a viewer for the game.")
public class Viewer  implements Runnable {
    // Game
    protected final Terminal terminal = new Terminal();
    protected final InputHandler inputHandler = new InputHandler(terminal, 50);
    private final int DISPLAY_FREQUENCY = 25;
    private String board = "";

    // Multicast stream
    private MulticastSocket multicastStreamSocket;
    @Option(names = {"-msh", "--multicast-stream-host"}, description = "Multicast stream host address.")
    private String multicastStreamHost = "239.1.1.2";
    @Option(names = {"-msp", "--multicast-stream-port"}, description = "Multicast stream port number.")
    private int multicastStreamPort = 20001;
    private InetAddress multicastStreamAddress;
    private InetSocketAddress multicastStreamGroup;
    private NetworkInterface multicastStreamNetworkInterface;
    private ScheduledExecutorService multicastStreamScheduledExecutorService;

    /**
     * Continuously receives multicast messages and updates the game board.
     */
    private void receiveMulticast() {
        try {
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );
                multicastStreamSocket.receive(packet);
                board = Message.getData(Message.getResponse(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8)));
                Thread.sleep(DISPLAY_FREQUENCY);
            }
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a thread to continuously receive multicast messages.
     */
    protected void startReceiveMulticast() {
        multicastStreamScheduledExecutorService.execute(this::receiveMulticast);
    }

    /**
     * Stops the thread that is receiving multicast messages.
     */
    private void stopReceiveMulticast() {
        multicastStreamScheduledExecutorService.shutdown();
    }

    /**
     * Print board
     */
    public void printBoard() {
        terminal.clear();
        terminal.print(board);
    }

    /**
     * Watches the game, displaying the board and waiting for the quit command.
     */
    private void watchGame(){
        while (inputHandler.getKey() != Key.QUIT) {
            printBoard();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while (inputHandler.getKey() != Key.ENTER) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        quit();
    }

    /**
     * Overrides the quit method to clear the terminal and wait for the enter key to exit.
     */
    protected void quit() {
        terminal.clear();
        terminal.print("Press enter to exit\n");

        while (inputHandler.getKey() != Key.ENTER) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        exit(0);
    }

    @Override
    public void run() {
        try {
            // Multicast
            this.multicastStreamSocket = new MulticastSocket(multicastStreamPort);
            this.multicastStreamAddress = InetAddress.getByName(multicastStreamHost);
            this.multicastStreamGroup = new InetSocketAddress(multicastStreamAddress, multicastStreamPort);
            NetworkInterfaceSelector selector = new NetworkInterfaceSelector();
            this.multicastStreamNetworkInterface = selector.selectNetworkInterface();
            this.multicastStreamSocket.joinGroup(multicastStreamGroup, multicastStreamNetworkInterface);
            this.multicastStreamScheduledExecutorService = Executors.newScheduledThreadPool(1);

            startReceiveMulticast();
            watchGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Viewer()).execute(args);
        System.exit(exitCode);
    }
}
