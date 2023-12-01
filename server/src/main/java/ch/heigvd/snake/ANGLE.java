package ch.heigvd.snake;

import ch.heigvd.DIRECTION;

public enum ANGLE {
    UP_RIGHT {
        @Override
        public String toString() {
            return "┓";
        }
    },
    UP_LEFT {
        @Override
        public String toString() {
            return "┏";
        }
    },
    DOWN_RIGHT {
        @Override
        public String toString() {
            return "┛";
        }
    },
    DOWN_LEFT {
        @Override
        public String toString() {
            return "┗";
        }
    };

    static char getAngle(DIRECTION direction, DIRECTION previousDirection){
        return switch (direction) {
            case UP -> switch (previousDirection) {
                case LEFT -> UP_RIGHT.toString().charAt(0);
                case RIGHT -> UP_LEFT.toString().charAt(0);
                default -> ' ';
            };
            case DOWN -> switch (previousDirection) {
                case LEFT -> DOWN_RIGHT.toString().charAt(0);
                case RIGHT -> DOWN_LEFT.toString().charAt(0);
                default -> ' ';
            };
            case LEFT -> switch (previousDirection) {
                case UP -> DOWN_LEFT.toString().charAt(0);
                case DOWN -> UP_LEFT.toString().charAt(0);
                default -> ' ';
            };
            case RIGHT -> switch (previousDirection) {
                case UP -> DOWN_RIGHT.toString().charAt(0);
                case DOWN -> UP_RIGHT.toString().charAt(0);
                default -> ' ';
            };
            default -> ' ';
        };
    }
}

