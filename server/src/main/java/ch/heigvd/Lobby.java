package ch.heigvd;

import ch.heigvd.snake.Snake;

import java.util.ArrayList;

import static ch.heigvd.DIRECTION.*;
/**
 * The class that represent the lobby
 */
public class Lobby {
    /**
     * The list of players in the lobby
     */
    private final ArrayList<Player> players;
    /**
     * The maximum number of players in the lobby
     */
    private final int maxPlayers;
    /**
     * The boolean that indicates if the lobby is open
     */
    private boolean isOpen = true;

    /**
     * Constructor
     * @param maxPlayers The maximum number of players in the lobby
     */
    public Lobby(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        players = new ArrayList<>();
    }


    /**
     * Add a player to the lobby
     * @param player The player to add
     */
    public void join(Player player) {
        for (Player p : players) {
            if (p.getName().equals(player.getName())) {
                return;
            }
        }
        if (players.size() < maxPlayers) {
            players.add(player);
        }
    }

    /**
     * Set the ready state of the player
     * @param player
     */
    public void setReady(Player player) {
        player.setReady();
    }

    /**
     * Get the ready players
     * @return The ready players
     */
    public ArrayList<Player> getReadyPlayers() {
        ArrayList<Player> readyPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.isReady()) {
                readyPlayers.add(player);
            }
        }
        return readyPlayers;
    }

    /**
     * Ask if the lobby is full
     * @return true if the lobby is full
     */
    public boolean lobbyIsFull() {
        return players.size() >= maxPlayers;
    }

    /**
     * Get the players in the lobby
     * @return The players in the lobby
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Ask if the lobby is open
     * @return true if the lobby is open
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Close the lobby
     */
    public void close() {
        isOpen = false;
    }

    /**
     * Get the number of players in the lobby
     * @return The number of players in the lobby
     */
    public int getNbPlayer() {
        return players.size();
    }

    /**
     * Ask if every player is ready
     * @return true if every player is ready
     */
    public boolean everyPlayerReady() {
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return !getReadyPlayers().isEmpty();
    }

    /**
     * Remove a player from the lobby
     * @param player The player to remove
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * Open the lobby
     */
    public void open() {
        isOpen = true;
    }

    /**
     * Ask if the player name is already in use
     * @param userName The name to check
     * @return true if the name is already in use
     */
    public boolean playerNameAlreadyInUse(String userName) {
        for (Player player : players) {
            if (player.getName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * init snakes for each player in the lobby
     * @param board the board to determine and compute the position of the snakes
     */
    public void initSnakes(Board board) {
        int initLenght = 4;
        Position initPosition;
        int bw = board.getWidth();
        int bh = board.getHeight();

        int i = 0;
        for (Player player : players) {
            switch (i) {
                case 0: {
                    initPosition = new Position(bw / 2, bh, UP, ' ');
                    player.setSnake(new Snake(initPosition, initLenght, i));
                    break;
                }
                case 1: {
                    initPosition = new Position(0, bh / 2, LEFT, ' ');
                    player.setSnake(new Snake(initPosition, initLenght, i));
                    break;
                }
                case 2: {
                    initPosition = new Position(bw / 2, 0, DOWN, ' ');
                    player.setSnake(new Snake(initPosition, initLenght, i));
                    break;
                }
                case 3: {
                    initPosition = new Position(bw, bh / 2, RIGHT, ' ');
                    player.setSnake(new Snake(initPosition, initLenght, i));
                    break;
                }
            }
            i++;
        }
    }

    /**
     * Set the direction of the player
     * @param player The player to set the direction
     * @param direction The direction to set
     */
    public void setDirection(Player player, DIRECTION direction) {
        player.getSnake().setNextDirection(direction);
    }

    /**
     * Move the snakes for each player
     */
    public void snakeStep() {
        for (Player player : players) {
            player.getSnake().step();
        }
    }

    /**
     * Get the snakes of the players
     * @return The snakes of the players
     */
    public ArrayList<Snake> getSnakes() {
        ArrayList<Snake> snakes = new ArrayList<>();
        for (Player player : players) {
            snakes.add(player.getSnake());
        }
        return snakes;
    }

    public String getInfos() {
        if (isOpen) {
            return "Press r to ready\n" +
                    "Press q to quit\n" +
                    "Press h to get help\n";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Player p : players) {
                sb.append(p.getName()).append(" : ").append(p.getInfo()).append("\n");
            }
            return sb.toString();
        }
    }
}
