package ch.heigvd.snake;

import ch.heigvd.Direction;

public enum Body {
    VERTICAL {
        @Override

        public String toString() {
            return "┃";
        }
    },
    HORIZONTAL {
        @Override
        public String toString() {
            return "━";
        }
    };
    static char getBody(Direction direction){
        return switch (direction) {
            case UP, DOWN -> VERTICAL.toString().charAt(0);
            case LEFT, RIGHT -> HORIZONTAL.toString().charAt(0);
        };
    }
}
