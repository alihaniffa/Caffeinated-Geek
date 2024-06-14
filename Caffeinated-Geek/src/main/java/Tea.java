import java.util.List;
import java.util.Set;

public class Tea extends Beverage {

    private int temperature;
    private int steepingTime;

    public Tea(DreamBeverage dreamBeverage, String id, String name, String description, double price, int temperature, int steepingTime) {
        super(dreamBeverage, id, name, description, price);
        this.temperature = temperature;
        this.steepingTime = steepingTime;
    }

    public Tea(DreamBeverage dreamBeverage, int temperature, int steepingTime) {
        super(dreamBeverage, "", "", "", 0);
        this.temperature = temperature;
        this.steepingTime = steepingTime;
    }

    public Tea(DreamBeverage dreamBeverage) {
        super(dreamBeverage, "", "", "", 0);
    }


    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getSteepingTime() {
        return steepingTime;
    }

    public void setSteepingTime(int steepingTime) {
        this.steepingTime = steepingTime;
    }
}
