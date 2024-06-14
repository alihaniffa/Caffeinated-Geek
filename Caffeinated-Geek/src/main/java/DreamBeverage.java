import java.util.List;
import java.util.Set;

/**
 * Represents the configuration of a dream beverage.
 */

public class DreamBeverage {

    private double[] priceRange = null;
    private SugarPreference sugar;
    private List<Milk> milkOptions;
    private Set<String> extras;
    private TypeOfBeverage type;

    /**
     * Constructs a DreamBeverage object.
     *
     * @param priceRange   The price range of the dream beverage.
     * @param sugar        The sugar preference of the dream beverage.
     * @param milkOptions  The milk options available for the dream beverage.
     * @param extras       The extra options available for the dream beverage.
     * @param type         The type of beverage.
     */
    public DreamBeverage(double[] priceRange, SugarPreference sugar, List<Milk> milkOptions, Set<String> extras, TypeOfBeverage type) {
        this.priceRange = priceRange;
        this.sugar = sugar;
        this.milkOptions = milkOptions;
        this.extras = extras;
        this.type = type;
    }



    public TypeOfBeverage getType() {
        return type;
    }

    public void setType(TypeOfBeverage type) {
        this.type = type;
    }

    public double[] getPriceRange() {
        return priceRange;
    }

    public SugarPreference getSugarPreference() {
        return sugar;
    }

    public void setSugar(SugarPreference sugar) {
        this.sugar = sugar;
    }

    public List<Milk> getMilkOptions() {
        return milkOptions;
    }

    public void setMilkOptions(List<Milk> milkOptions) {
        this.milkOptions = milkOptions;
    }

    public Set<String> getExtras() {
        return extras;
    }

    public void setExtras(Set<String> extras) {
        this.extras = extras;
    }

}
