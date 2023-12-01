package ch.heigvd;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class that represent the server
 */
public class Server {
    /**
     * The number of players in the game
     */
    private static final int NB_PLAYER = 4;

    /**
     * The logger
     */
    private final static Logger LOG = Logger.getLogger(Server.class.getName());

    /**
     * The frequency that refresh the game in milliseconds
     */
    private final int GAME_FREQUENCY = 200;

    /**
     * The port used by the server
     */
    private final int port;
    /**
     * Pool of thread
     */

    ArrayList<Thread> pool = new ArrayList<Thread>();
    /**
     * The lobby
     */
    private Lobby lobby = new Lobby(NB_PLAYER);
    /**
     * The boolean that indicates if the server is listening for new clients
     */
    private boolean listenNewClient = true;
    /**
     * The board
     */
    private Board board;
    /**
     * The directions initialized in the order UP, RIGHT, DOWN, LEFT for the first 4 players
     */
    private DIRECTION[] directions = {DIRECTION.UP, DIRECTION.RIGHT, DIRECTION.DOWN, DIRECTION.LEFT};

    /**
     * The constructor
     *
     * @param port The port used by the server
     */
    private Server(int port) {
        this.port = port;
    }


    /**
     * Set the direction of the player
     *
     * @param key    The key pressed by the player
     * @param player The player that pressed the key
     */
    public void setDirection(KEY key, Player player) {
        if (!lobby.everyPlayerReady()) return;
        DIRECTION direction = DIRECTION.parseKey(key);
        if (direction != null) {
            lobby.setDirection(player, direction);
        }
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
     * Get the board
     *
     * @return The board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Ask if lobby is full
     *
     * @return true if lobby is full
     */
    public boolean isFull() {
        return lobby.lobbyIsFull();
    }

    /**
     * remove player from lobby
     */
    public void removePlayer(Player player) {
        lobby.removePlayer(player);
    }

    /**
     * Set the player ready
     *
     * @param player The player that is ready
     */
    public void setReady(Player player) {
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
     * Get the frequency that refresh the game in milliseconds
     *
     * @return The frequency that refresh the game in milliseconds
     */
    public int getGameFrequency() {
        return GAME_FREQUENCY;
    }

    /**
     * Get the infos of the lobby
     *
     * @return The infos of the lobby
     */
    public String getInfos() {
        return lobby.getInfos();
    }

    /**
     * Start the server
     */
    private void start() {

        while (true) {
            listenNewClient = true;
            Thread thListener = new Thread(this::listenNewClient);
            thListener.start();

            board = new Board(30, 15, 15, 200);

            //loop for lobby
            lobby.open();
            while (!lobby.everyPlayerReady()) {
                board.deployLobby(lobby);
                try {
                    Thread.sleep(GAME_FREQUENCY);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            lobby.initSnakes(board);
            lobby.close();
            listenNewClient = false;
            thListener.interrupt();

            ArrayList<Position> generatedFood = new ArrayList<>();
            //loop for game
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

    /**
     * Listen for new clients
     */
    private void listenNewClient() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (listenNewClient) {
                LOG.log(Level.INFO, "Waiting for a new client on port {0}", port + " with PID: " + ProcessHandle.current().pid());
                Socket clientSocket = serverSocket.accept();

                if (lobby.isOpen() && !lobby.lobbyIsFull()) {
                    LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
                    Thread th = new Thread(new ServerWorker(clientSocket, this));
                    pool.add(th);
                    th.start();
                    continue;
                }

                BufferedWriter serverOutput = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

                if (lobby.lobbyIsFull()) {
                    LOG.log(Level.INFO, "The lobby is full. Rejecting the new client...");
                    serverOutput.write(Message.setCommand(Message.EROR, "The lobby is full"));
                    serverOutput.flush();
                    clientSocket.close();
                    continue;
                } else {
                    LOG.log(Level.INFO, "The lobby is closed. Rejecting the new client...");
                    serverOutput.write(Message.setCommand(Message.EROR, "The lobby is closed"));
                    serverOutput.flush();
                    clientSocket.close();
                    continue;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        int port = 20000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
        (new Server(port)).start();
    }
}