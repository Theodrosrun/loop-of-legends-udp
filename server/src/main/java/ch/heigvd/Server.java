package ch.heigvd;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "Server", mixinStandardHelpOptions = true, version = "Server 1.0",
        description = "Starts the game server.")
public class Server implements Runnable {
    // Game configuration
    private static final int NB_PLAYERS = 4;
    private UUID uuid;
    private Board board;
    private static final int GAME_FREQUENCY = 200;
    private final Lobby lobby = new Lobby(NB_PLAYERS);

    // Unicast
    private static final int UNICAST_NB_EXECUTORS = 1;
    private static final int UNICAST_NB_THREADS = 10;
    private DatagramSocket unicastSocket;
    @Option(names = {"-up", "--unicast-port"}, description = "Unicast port number.")
    private int unicastPort = 10000;
    private ExecutorService unicastExecutorService;

    // Mulitcast
    private static final long INIT_DELAY = 100;
    private static final int PERIOD = GAME_FREQUENCY / 2;
    private MulticastSocket multicastSocket;
    @Option(names = {"-mh", "--multicast-host"}, description = "Multicast host address.")
    private String multicastHost = "239.1.1.1";
    @Option(names = {"-mp", "--multicast-port"}, description = "Multicast port number.")
    private int multicastPort = 20000;
    private InetAddress multicastAddress;
    private InetSocketAddress multicastGroup;

    // Mutlicast stream
    private MulticastSocket multicastStreamSocket;
    @Option(names = {"-msh", "--multicast-stream-host"}, description = "Multicast stream host address.")
    private String multicastStreamHost = "239.1.1.2";
    @Option(names = {"-msp", "--multicast-stream-port"}, description = "Multicast stream port number.")
    private int multicastStreamPort = 20001;
    private InetAddress multicastStreamAddress;
    private InetSocketAddress multicastStreamGroup;

    // Mulitcast config
    private NetworkInterface multicastNetworkInterface;
    private ScheduledExecutorService multicastScheduledExecutorService;

    /**
     * Retrieves a player by their UUID.
     *
     * @param uuid UUID of the player.
     * @return The player corresponding to the given UUID.
     */
    Player getPlayerByUUID(UUID uuid){
        return lobby.getPlayerByUUID(uuid);
    }

    /**
     * Sends multicast messages at regular intervals.
     * Includes game and lobby information in the message.
     */
    private void sendMulticast() {
        multicastScheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                StringBuilder sb = new StringBuilder(board.toString());
                sb.append("\n");
                sb.append(getLobbyInfos());
                String message = Message.setCommand(uuid, Message.UPTE, sb.toString());

                byte[] payload = message.getBytes(StandardCharsets.UTF_8);

                DatagramPacket datagram = new DatagramPacket(
                        payload,
                        payload.length,
                        multicastGroup
                );

                DatagramPacket datagramStream = new DatagramPacket(
                        payload,
                        payload.length,
                        multicastStreamGroup
                );

                multicastSocket.send(datagram);
                multicastStreamSocket.send(datagramStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, INIT_DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops sending multicast messages by shutting down the scheduled executor service.
     */
    private void stopSendMulticast() {
        multicastScheduledExecutorService.shutdown();
    }

    /**
     * Gets the UUID of the server.
     *
     * @return UUID of the server.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Join the lobby
     *
     * @param player The player that wants to join the lobby
     */
    public void joinLobby(Player player) {
        lobby.join(player);
        board.deployLobby(lobby);
    }

    /**
     * Ask if lobby is full
     *
     * @return true if lobby is full
     */
    public boolean isLobbyFull() {
        return lobby.lobbyIsFull();
    }

    /**
     * Get the infos of the lobby
     *
     * @return The infos of the lobby
     */
    public String getLobbyInfos() {
        return lobby.getInfos();
    }

    /**
     * remove player from lobby
     */
    public void removePlayerFromLobby(Player player) {
        lobby.removePlayer(player);
    }

    /**
     * Set the player ready
     *
     * @param player The player that is ready
     */
    public void setPlayerReady(Player player) {
        lobby.setReady(player);
        board.deployLobby(lobby);
    }

    /**
     * Ask if the username is already in use
     *
     * @return true if the username is already in use
     */
    public boolean playerNameAlreadyInUse(String userName) {
        return lobby.playerNameAlreadyInUse(userName);
    }

    /**
     * Set the direction of the player
     *
     * @param key    The key pressed by the player
     * @param player The player that pressed the key
     */
    public void setDirection(Key key, Player player) {
        if (!lobby.everyPlayerReady()) return;
        Direction direction = Direction.parseKey(key);
        if (direction != null) {
            lobby.setDirection(player, direction);
        }
    }

    /**
     * Start the server
     */
    private void start() {
        while (true) {

            board = new Board(30, 15, 15, 200);

            // Loop for lobby
            lobby.open();
            while (!lobby.everyPlayerReady()) {
                board.deployLobby(lobby);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            lobby.initSnakes(board);
            lobby.close();

            // Loop for game
            ArrayList<Position> generatedFood = new ArrayList<>();
            while (lobby.getNbPlayer() > 0) {
                board.initBoard();
                generatedFood.clear();
                lobby.snakeStep();
                for (Player player : lobby.getPlayers()) {
                    if (!player.isAlive()) continue;
                    for (Player opponent : lobby.getPlayers()) {
                        if (player != opponent) {
                            {
                                generatedFood.addAll(opponent.getSnake().attack(player.getSnake()));
                            }
                        }
                    }
                }
                board.setFood(generatedFood);
                board.deploySnakes(lobby.getSnakes());
                board.deployFood();

                try {
                    Thread.sleep(GAME_FREQUENCY);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            uuid = UUID.randomUUID();

            // Unicast
            this.unicastSocket = new DatagramSocket(unicastPort);
            this.unicastExecutorService = Executors.newFixedThreadPool(UNICAST_NB_EXECUTORS);
            this.unicastExecutorService.submit(new ServerReceiver(this, unicastSocket, UNICAST_NB_THREADS));

            // Multicast
            this.multicastSocket = new MulticastSocket(multicastPort);
            this.multicastAddress = InetAddress.getByName(multicastHost);
            this.multicastGroup = new InetSocketAddress(multicastAddress, multicastPort);
            NetworkInterfaceSelector selector = new NetworkInterfaceSelector();
            this.multicastNetworkInterface = selector.selectNetworkInterface();
            this.multicastSocket.joinGroup(multicastGroup, multicastNetworkInterface);
            this.multicastScheduledExecutorService = Executors.newScheduledThreadPool(UNICAST_NB_EXECUTORS);

            // Multicast stream
            this.multicastStreamSocket = new MulticastSocket(multicastStreamPort);
            this.multicastStreamAddress = InetAddress.getByName(multicastStreamHost);
            this.multicastStreamGroup = new InetSocketAddress(multicastStreamAddress, multicastStreamPort);
            this.multicastStreamSocket.joinGroup(multicastStreamGroup, multicastNetworkInterface);

            sendMulticast();
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Server()).execute(args);
        System.exit(exitCode);
    }
}
