package ch.heigvd;

/**
 * The enum that represent the position on the map
 */
public enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    /**
     * The parser that convert a key to a direction
     */
    public static DIRECTION parseKey(KEY key){

        switch (key) {
            case UP -> {
                return DIRECTION.UP;
            }
            case DOWN -> {
                return DIRECTION.DOWN;
            }
            case LEFT -> {
                return DIRECTION.LEFT;
            }
            case RIGHT -> {
                return DIRECTION.RIGHT;
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
    public static int[] getCoef(DIRECTION direction){
        return switch (direction) {
            case UP -> new int[]{0, -1};
            case DOWN -> new int[]{0, 1};
            case LEFT -> new int[]{-1, 0};
            case RIGHT -> new int[]{1, 0};
            default -> new int[]{0, 0};
        };
    }
}
