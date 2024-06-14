import java.util.List;
import java.util.Set;

/**
 * Represents a beverage item available in the menu.
 */
public class Beverage {

    private DreamBeverage dreamBeverage;
    private String id;
    private String name;
    private String description;
    private double price;

    /**
     * Constructs a new Beverage object.
     *
     * @param dreamBeverage The dream beverage configuration.
     * @param id            The ID of the beverage.
     * @param name          The name of the beverage.
     * @param description   The description of the beverage.
     * @param price         The price of the beverage.
     */
    public Beverage(DreamBeverage dreamBeverage, String id, String name, String description, double price) {
        this.dreamBeverage = dreamBeverage;
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public DreamBeverage getDreamBeverage() {
        return dreamBeverage;
    }

    public void setDreamBeverage(DreamBeverage dreamBeverage) {
        this.dreamBeverage = dreamBeverage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Returns a string representation of the beverage.
     *
     * @return A string representation of the beverage.
     */
    @Override
    public String toString() {
        String f = String.format("%s: \n%s", this.getName(), this.getDescription());
        return f;
    }
}
