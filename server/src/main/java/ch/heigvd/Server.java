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

public class Server {
    // Game configuration
    private static final int NB_PLAYERS = 4;
    private boolean listenNewClient = true;
    private Board board;

    // UUID for unicast communication
    private UUID uuid;
    private static final int GAME_FREQUENCY = 200;

    private final Lobby lobby = new Lobby(NB_PLAYERS);

    // Unicast
    private static final int UNICAST_NB_EXECUTORS = 1;
    private static final int UNICAST_NB_THREADS = 10;
    private DatagramSocket unicastSocket;
    private ExecutorService unicastExecutorService;

    // Mulitcast
    private static final int MAX_PACKET_SIZE = 1024;
    private static final long INIT_DELAY = 100;
    private static final int PERIOD = GAME_FREQUENCY / 2;
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private InetSocketAddress multicastGroup;
    private NetworkInterface multicastNetworkInterface;
    private ScheduledExecutorService multicastScheduledExecutorService;

    Server(int unicastPort, int multicastPort, String multicastHost) {
        uuid = UUID.randomUUID();
        try {
            // Unicast
            this.unicastSocket = new DatagramSocket(unicastPort);
            this.unicastExecutorService = Executors.newFixedThreadPool(UNICAST_NB_EXECUTORS);
            this.unicastExecutorService.submit(new ServerReceiver(this, unicastSocket, UNICAST_NB_THREADS));

            // Multicast
            this.multicastSocket = new MulticastSocket(multicastPort);
            this.multicastAddress = InetAddress.getByName(multicastHost);
            this.multicastGroup = new InetSocketAddress(multicastAddress, multicastPort);
            this.multicastNetworkInterface = NetworkInterfaceHelper.getFirstNetworkInterfaceAvailable();
            this.multicastSocket.joinGroup(multicastGroup, multicastNetworkInterface);
            this.multicastScheduledExecutorService = Executors.newScheduledThreadPool(UNICAST_NB_EXECUTORS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Player getPlayerByUUID(UUID uuid){
        return lobby.getPlayerByUUID(uuid);
    }

    private void stopAcceptClient() {
        unicastExecutorService.shutdown();
    }

    // Passive discovery protocol pattern
    private void sendMulticast() {
        multicastScheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                StringBuilder sb = new StringBuilder(getBoard().toString());
                sb.append("\n");
                sb.append(getLobbyInfos());
                String message = Message.setCommand(uuid, Message.UPTE, sb.toString());

                byte[] payload = message.getBytes(StandardCharsets.UTF_8);

                DatagramPacket datagram = new DatagramPacket(
                        payload,
                        payload.length,
                        multicastGroup
                );

                multicastSocket.send(datagram);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, INIT_DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }

    private void stopSendMulticast() {
        multicastScheduledExecutorService.shutdown();
    }

    public void joinLobby(Player player) {
        lobby.join(player);
        board.deployLobby(lobby);
    }

    public boolean isLobbyFull() {
        return lobby.lobbyIsFull();
    }

    public String getLobbyInfos() {
        return lobby.getInfos();
    }

    public void removePlayerFromLobby(Player player) {
        lobby.removePlayer(player);
    }

    public void setPlayerReady(Player player) {
        lobby.setReady(player);
        board.deployLobby(lobby);
    }

    public boolean playerNameAlreadyInUse(String userName) {
        return lobby.playerNameAlreadyInUse(userName);
    }

    public void setDirection(Key key, Player player) {
        if (!lobby.everyPlayerReady()) return;
        Direction direction = Direction.parseKey(key);
        if (direction != null) {
            lobby.setDirection(player, direction);
        }
    }

    public Board getBoard() {
        return board;
    }

    private void start() {
        while (true) {
//            listenNewClient = true;
//            Thread thListener = new Thread(this::listenNewClient);
//            thListener.start();
              board = new Board(30, 15, 15, 200);
//
//            // Loop for lobby
            lobby.open();
            while (!lobby.everyPlayerReady()) {
                board.deployLobby(lobby);
            }
            lobby.initSnakes(board);
            lobby.close();
//            listenNewClient = false;
//            thListener.interrupt();

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

    public static void main(String[] args) {
        // Unicast
        int unicastPort = 10000;

        // Multicast
        String multicastHost = "239.1.1.1";
        int multicastPort = 20000;

        Server server = new Server(unicastPort, multicastPort, multicastHost);
        server.sendMulticast();
        server.start();
    }

    public UUID getUuid() {
        return uuid;
    }
}
