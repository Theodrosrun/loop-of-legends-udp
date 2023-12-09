package ch.heigvd;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    // Game configuration
    private static final int NB_PLAYERS = 4;
    private static final long INIT_DELAY = 1000;
    private static final int PERIOD = 200;
    private Lobby lobby = new Lobby(NB_PLAYERS);
    private boolean listenNewClient = true;
    private Board board;
    private Direction[] directions = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};

    // Unicast
    private static final int UNICAST_NB_EXECUTORS = 1;
    private static final int UNICAST_NB_THREADS = 10;
    private DatagramSocket unicastSocket;
    private ExecutorService unicastExecutorService;

    // Mulitcast
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private InetSocketAddress multicastGroup;
    private NetworkInterface multicastNetworkInterface;
    private ScheduledExecutorService multicastScheduledExecutorService;

    Server(int unicastPort, int multicastPort, String multicastHost) {
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

    // Passive discovery protocol pattern
    private void sendMulticast() {
        multicastScheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                String message = "Server is emitting";

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

    public static void main(String[] args) {
        // Unicast
        int unicastPort = 10000;

        // Multicast
        String multicastHost = "239.1.1.1";
        int multicastPort = 20000;

        Server server = new Server(unicastPort, multicastPort, multicastHost);
        server.sendMulticast();
    }
}
