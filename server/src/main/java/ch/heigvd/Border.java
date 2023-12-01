package ch.heigvd;

/**
 * The enum that represent the piece of border of the map
 */
public enum Border {
    HORIZONTAL,
    VERTICAL,
    CORNER_TOP_LEFT,
    CORNER_TOP_RIGHT,
    CORNER_BOTTOM_LEFT,
    CORNER_BOTTOM_RIGHT;

    /**
     * The char that represent the border
     */
    private char[] borderChars;

    static {
        HORIZONTAL.borderChars = new char[]{'═', '─', '━'};
        VERTICAL.borderChars = new char[]{'║', '│', '┃'};
        CORNER_TOP_LEFT.borderChars = new char[]{'╔', '┌', '┏'};
        CORNER_TOP_RIGHT.borderChars = new char[]{'╗', '┐', '┓'};
        CORNER_BOTTOM_LEFT.borderChars = new char[]{'╚', '└', '┗'};
        CORNER_BOTTOM_RIGHT.borderChars = new char[]{'╝', '┘', '┛'};
    }

    /**
     * Get the char that represent the border
     * @param borderType The type of border
     * @return The char that represent the border
     */
    public char getBorder(BorderType borderType) {
        return borderChars[borderType.ordinal()];
    }
}

