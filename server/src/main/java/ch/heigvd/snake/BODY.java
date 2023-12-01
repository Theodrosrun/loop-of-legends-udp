package ch.heigvd.snake;

import ch.heigvd.DIRECTION;

public enum BODY {
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
    static char getBody(DIRECTION direction){
        return switch (direction) {
            case UP, DOWN -> VERTICAL.toString().charAt(0);
            case LEFT, RIGHT -> HORIZONTAL.toString().charAt(0);
        };
    }
}
