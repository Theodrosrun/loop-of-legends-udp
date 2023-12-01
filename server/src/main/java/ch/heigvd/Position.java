package ch.heigvd;

import static java.lang.Math.abs;

/**
 * The class that represent the position on the map
 */
public class Position {
    private static int limit_x = 0;
    private static int limit_y = 0;

    /**
     * The x and y coordinates of the position
     */
    private final int x;

    /**
     *  The x and y coordinates of the position
     */
    private final int y;

    /**
     * The direction of the position
     */
    private DIRECTION direction;

    /**
     * The representation of the position on board
     */
    private char representation;

    /**
     * Constructor
     * @param x The x coordinate of the position
     * @param y The y coordinate of the position
     * @param representation The representation of the position on board
     */
    public Position(int x, int y, char representation) {
        this.x = x;
        this.y = y;
        this.direction = null;
        this.representation = representation;
    }

    /**
     * Constructor
     * @param x The x coordinate of the position
     * @param y The y coordinate of the position
     * @param direction The direction of the position
     * @param representation The representation of the position on board
     */
    public Position(int x, int y, DIRECTION direction, char representation) {
        this.x = getRelativeX(x);
        this.y = getRelativeY(y);
        this.direction = direction;
        this.representation = representation;
    }

    public static double getLimit_x() {
        return limit_x;
    }

    public static double getLimit_y() {
        return limit_y;
    }

    /**
     * get the x coordinate of the position
     * @return The x coordinate of the position
     */
    public int getX() {
        return x;
    }

    /**
     * get the y coordinate of the position
     * @return The y coordinate of the position
     */
    public int getY() {
        return y;
    }

    /**
     * get the direction of the position
     * @return The direction of the position
     */
    public DIRECTION getDirection() {
        return direction;
    }

    /**
     * set the direction of the position
     * @param direction The direction of the position
     */
    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    /**
     * get the representation of the position on board
     * @return The representation of the position on board
     */
    public char getRepresentation() {
        return representation;
    }

    /**
     * set the representation of the position on board
     * @param representation The representation of the position on board
     */
    public void setRepresentation(char representation) {
        this.representation = representation;
    }

    /**
     * get the relative value of the given value
     *
     * @param value the value to get the relative value
     * @param limit the limit of the value
     * @return the relative value of the given value
     */
    private static int getRelativeValue(int value, int limit) {

        int relativeValue = value % limit;

        if (value % limit < 0) {
            relativeValue = limit - (abs(relativeValue));
        }
        return relativeValue;

    }
    /**
     * get the relative x position of the board
     *
     * @param x the x position
     * @return the relative x position of the board
     */
    public static int getRelativeX(int x) {
        return getRelativeValue(x, limit_x);
    }

    /**
     * get the relative y position of the board
     *
     * @param y the y position
     * @return the relative y position of the board
     */
    public static int getRelativeY(int y) {
        return getRelativeValue(y, limit_y);
    }

    /**
     * Compare two positions with their coordinates
     * @param obj The position to compare
     * @return true if the positions are equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position position) {
            return position.getX() == this.getX() && position.getY() == this.getY();
        }
        return false;
    }

    public static void setLimit_x(int limitX) {
        limit_x = limitX;
    }

    public static void setLimit_y(int limitY) {
        limit_y = limitY;
    }
}
