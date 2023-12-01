package ch.heigvd;

import ch.heigvd.snake.Snake;

/**
 * The class that represent the player
 */
public class Player {

    /**
     * The counter of the player
     */
    private static int idCnt = 0;

    /**
     * The id of the player
     */
    private final int id;

    /**
     * The snake of the player
     */
    private Snake snake;

    /**
     * The name of the player
     */
    private final String name;

    /**
     * The ready state of the player
     */
    private boolean ready = false;

    /**
     * Constructor
     * @param name The name of the player
     */
    public Player(String name) {
        this.id = ++idCnt;
        this.name = name;
    }

    /**
     * Get the name of the player
     * @return The name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Get the snake of the player
     * @return The snake of the player
     */
    public Snake getSnake() {
        return snake;
    }

    /**
     * Set the snake of the player
     * @param snake The snake of the player
     */
    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    /**
     * Set the ready state of the player
     */
    public void setReady() {
        this.ready = !this.ready;
    }

    /**
     * Get the ready state of the player
     * @return The ready state of the player
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Get the id of the player
     * @return The id of the player
     */
    public int getId() {
        return id;
    }

    public String getInfo() {
        return snake.getInfo();
    }

    public boolean isAlive() {
        return snake.isAlive();
    }

    /**
     * compare two players with their id
     * @param obj The player to compare
     * @return true if the players are equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player player){
            return this.id == player.id;
        }
        return false;
    }
}
