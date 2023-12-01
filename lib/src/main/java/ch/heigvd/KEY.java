package ch.heigvd;

import com.googlecode.lanterna.input.KeyStroke;

/**
 * Enumeration of the different keys that can be pressed by the user
 * during the game.
 */
public enum KEY {
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
    public static KEY parseKeyStroke(KeyStroke key) {
        if (key == null) {
            return KEY.NONE;
        }
        return switch (key.getKeyType()) {
            case ArrowUp -> KEY.UP;
            case ArrowDown -> KEY.DOWN;
            case ArrowLeft -> KEY.LEFT;
            case ArrowRight -> KEY.RIGHT;
            case Enter -> KEY.ENTER;
            case Character -> switch (key.getCharacter()) {
                case 'q' -> KEY.QUIT;
                case 'r' -> KEY.READY;
                case 'h' -> KEY.HELP;
                default -> KEY.NONE;
            };
            default -> KEY.NONE;
        };
    }
}
