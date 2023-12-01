package ch.heigvd;

/**
 * The enum that represent the position on the map
 */
public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    /**
     * The parser that convert a key to a direction
     */
    public static Direction parseKey(Key key){

        switch (key) {
            case UP -> {
                return Direction.UP;
            }
            case DOWN -> {
                return Direction.DOWN;
            }
            case LEFT -> {
                return Direction.LEFT;
            }
            case RIGHT -> {
                return Direction.RIGHT;
            }
            default -> {
                return null;
            }

        }
    }

    /**
     * Get the coef of the direction
     * @param direction The direction to get the coef
     * @return The coef of the direction
     */
    public static int[] getCoef(Direction direction){
        return switch (direction) {
            case UP -> new int[]{0, -1};
            case DOWN -> new int[]{0, 1};
            case LEFT -> new int[]{-1, 0};
            case RIGHT -> new int[]{1, 0};
            default -> new int[]{0, 0};
        };
    }
}
