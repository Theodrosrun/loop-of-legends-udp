package ch.heigvd;

import com.googlecode.lanterna.input.KeyStroke;

/**
 * Enumeration of the different keys that can be pressed by the user
 * during the game.
 */
public enum Key {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    QUIT,
    ENTER,
    READY,
    HELP,
    NONE;

    /**
     * Parse a KeyStroke into a KEY
     * @param key the KeyStroke to parse
     * @return the corresponding KEY
     */
    public static Key parseKeyStroke(KeyStroke key) {
        if (key == null) {
            return Key.NONE;
        }
        return switch (key.getKeyType()) {
            case ArrowUp -> Key.UP;
            case ArrowDown -> Key.DOWN;
            case ArrowLeft -> Key.LEFT;
            case ArrowRight -> Key.RIGHT;
            case Enter -> Key.ENTER;
            case Character -> switch (key.getCharacter()) {
                case 'q' -> Key.QUIT;
                case 'r' -> Key.READY;
                case 'h' -> Key.HELP;
                default -> Key.NONE;
            };
            default -> Key.NONE;
        };
    }
}
