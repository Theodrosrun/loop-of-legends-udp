package ch.heigvd.snake;

import ch.heigvd.DIRECTION;
import ch.heigvd.Position;

import java.util.ArrayList;
import java.util.LinkedList;

public class Snake {

    /**
     * This array represent the head of the snake for each player
     */
    public final static char[] HEAD = {'∅','0','●','⦿'};

    /**
     * define if the snake is alive or not
     */
    boolean alive = true;

    /**
     * define if the snake is eating or not
     */
    boolean eat = false;

    /**
     * The score of the snake
     */
    private int score = 0;

    /**
     * the player number
     */
    int id;

    /**
     * the list of positions that represent the body of the snake
     */
    private final LinkedList<Position> body = new LinkedList<>();

    /**
     * The constructor of the snake
     * @param initialPosition the initial position of the snake
     * @param initialLength the initial length of the snake
     */
    public Snake(Position initialPosition, int initialLength, int id) {
        this.id = id;
        Position previousPosition = null;
        for (int i = initialLength; i > 0; --i) {
            int x = initialPosition.getX() + i * DIRECTION.getCoef(initialPosition.getDirection())[0];
            int y = initialPosition.getY() + i * DIRECTION.getCoef(initialPosition.getDirection())[1];
            Position position = new Position(x, y, initialPosition.getDirection(), getBodyRepresentation(initialPosition, previousPosition));
            previousPosition = position;
            body.add(position);
        }
    }

    /**
     * Set the next direction of the snake
     * @param direction the next direction
     */
    public void setNextDirection(DIRECTION direction) {
        if (!verifyNextDirection(direction)) return;
        body.getFirst().setDirection(direction);
    }

    /**
     * Get the direction of the snake
     * @return the direction of the snake
     */
    public DIRECTION getDirection() {
        return body.getFirst().getDirection();
    }

    /**
     * Get the head position of the snake
     * @return the head position of the snake
     */
    public Position getHead() {
        return body.getFirst();
    }

    /**
     * Get the body of the snake
     * @return the body of the snake
     */
    public LinkedList<Position> getPositions() {
        return body;
    }

    /**
     * moves the snake one step forward (step... lol... a snake... get it? YES I KNOW IT'S NOT FUNNY)
     */
    public void step(){
        Position head = body.getFirst();
        if (!alive) return;
        head.setRepresentation(getBodyRepresentation(head, head));

        Position newHead = new Position(
                head.getX() + DIRECTION.getCoef(head.getDirection())[0],
                head.getY() + DIRECTION.getCoef(head.getDirection())[1],
                head.getDirection(), HEAD[id]);
        body.addFirst(newHead);
        if (!eat){
            body.removeLast();
        } else {
            score++;
            eat = false;
        }
        for (int i = 2; i < body.size(); i++) {
            body.get(i-1).setRepresentation(getBodyRepresentation(body.get(i), body.get(i - 1)));
        }
        checkAutoCollision();
    }

    /**
     * Grow the snake
     */
    public void grow() {
        eat = true;
    }

    /**
     * Get the score of the snake
     * @return the score string of the snake
     */
    public String getInfo() {
        if (!alive) return "DEAD";
        return Integer.toString(score);
    }

    /**
     * Define if the snake is alive or not
     * @return true if the snake is alive, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Verify if the next direction is valid
     * @param direction the next direction
     * @return true if the next direction is valid, false otherwise
     */
    private boolean verifyNextDirection(DIRECTION direction) {
        return switch (direction) {
            case UP -> getDirection() != DIRECTION.DOWN;
            case DOWN -> getDirection() != DIRECTION.UP;
            case LEFT -> getDirection() != DIRECTION.RIGHT;
            case RIGHT -> getDirection() != DIRECTION.LEFT;
        };
    }

    /**
     * Get the representation of the body
     * @param position the position of the body
     * @param previousPosition the previous position of the body
     * @return the representation of the body
     */
    private char getBodyRepresentation(Position position, Position previousPosition) {
        if (previousPosition == null) {
            return HEAD[id];
        }
        if (position.getDirection() == previousPosition.getDirection()) {
            return BODY.getBody(position.getDirection());
        } else {
            return ANGLE.getAngle(position.getDirection(), previousPosition.getDirection());
        }
    }

    /**
     * Check if the snake is in collision with itself
     * @return true if the snake is in collision with itself, false otherwise
     */
    private void checkAutoCollision() {
        ;
        for (int i = 1; i < body.size(); i++) {
            if (body.get(i).equals(getHead())) {
                alive = false;
                break;
            }
        }
    }

    /**
     * Check if the snake is in collision with another snake
     * @param attackant the snake to check
     * @return the list of position of tail of the snake
     * */
    public ArrayList<Position> attack(Snake attackant) {
        ArrayList<Position> tail = new ArrayList<>();
        if (attackant.getHead().equals(getHead())) {
            alive = false;
            attackant.setAlive(false);
            return tail;
        }
        for (int i = 1; i < body.size(); i++) {
            if (attackant.getHead().equals(body.get(i))) {
                for(int j = i; j < body.size(); j++) {
                    tail.add(body.get(j));
                }
                body.subList(i, body.size()).clear();
                score -= (tail.size() / 2);
                break;
            }
        }
        return tail;
    }

    /**
     * Set the alive state of the snake
     * @param isAlive the alive state of the snake
     */
    private void setAlive(boolean isAlive) {
        alive = isAlive;
    }
}
