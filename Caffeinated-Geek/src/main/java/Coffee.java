import java.util.Set;

public class Coffee extends Beverage {

    private int numberOfShots;

    /**
     * Constructs a new Coffee object.
     *
     * @param dreamBeverage The dream beverage configuration.
     * @param numberOfShots The number of shots in the coffee.
     */
    public Coffee(DreamBeverage dreamBeverage, int numberOfShots) {
        super(dreamBeverage, "", "", "", 0);
        this.numberOfShots = numberOfShots;
    }

    /**
     * Constructs a new Coffee object with additional parameters.
     *
     * @param dreamBeverage The dream beverage configuration.
     * @param id            The ID of the coffee.
     * @param name          The name of the coffee.
     * @param description   The description of the coffee.
     * @param price         The price of the coffee.
     * @param numberOfShots The number of shots in the coffee.
     */
    public Coffee(DreamBeverage dreamBeverage, String id, String name, String description, double price, int numberOfShots) {
        super(dreamBeverage, id, name, description, price);
        this.numberOfShots = numberOfShots;
    }

    public int getNumberOfShots() {
        return numberOfShots;
    }

    public void setNumberOfShots(int numberOfShots) {
        this.numberOfShots = numberOfShots;
    }


}
