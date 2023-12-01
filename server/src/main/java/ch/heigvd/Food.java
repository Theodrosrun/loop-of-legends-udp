package ch.heigvd;
import java.util.ArrayList;

/**
 * The class that represent the food on the map
 */
public class Food {

    /**
     * the list of food present on the map
     */
    private final ArrayList<Position> food;

    /**
     * the list of food that has been eated
     */
    private final ArrayList<Position> eatedFood = new ArrayList<>();

    /**
     * the representation of the food
     */
    private final char representation = '⭑';

    /**
     * the representation of an empty space
     */
    private final char emptyChar = ' ';

    /**
     * the frequency of the food generation
     */
    private final int frequency;

    /**
     * the quantity of food generated each time
     */
    private final int quantity;

    /**
     * the time counter that generate the food
     */
    private int counter = 0;

    /**
     * Constructor
     * @param quantity the quantity of food generated each time
     * @param frequency the frequency of the food generation
     */
    public Food(int quantity, int frequency) {
        food = new ArrayList<>(quantity);
        this.frequency = frequency;
        this.quantity = quantity;
    }

    /**
     * Used to set food
     * @param generatedFood the generated food
     */
    public void setFood( ArrayList<Position> generatedFood) {
        for (Position f : food) {
            f.setRepresentation(representation);
        }
        food.addAll(generatedFood);
    }

    /**
     * get the list of food
     * @return the list of food
     */
    public ArrayList<Position> getFood(){
        generateFood();
        return food;
    }

    /**
     * remove the food at the given position
     * @param position the position of the food to remove
     */
    public void removeFood(Position position){

        for (int i = 0; i < food.size(); i++) {
            Position f = food.get(i);
            if (f.equals(position)) {
                f.setRepresentation(emptyChar);
                eatedFood.add(f);
                food.remove(f);
                return;
            }
        }

        for (Position f : food) {
            if (f.equals(position)) {

                return;
            }
        }
    }

    /**
     * check if the food at the given position has been eated
     * @param position the position of the food to check
     * @return true if the food has been eated, false otherwise
     */
    public boolean isEated(Position position){
        for (Position f : eatedFood) {
            if (f.equals(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * generate the food
     */
    private void generateFood(){
        eatedFood.clear();
        if (counter++ % frequency == 0) {
            for (int i = 0; i < quantity; i++) {
                food.add(getRandPosition());
            }
        }
    }

    /**
     * get a random position
     * @return a random position
     */
    private Position getRandPosition(){
        int x = (int) (Math.random() * Position.getLimit_x());
        int y = (int) (Math.random() * Position.getLimit_y());
        return new Position(x, y, representation);
    }
}
