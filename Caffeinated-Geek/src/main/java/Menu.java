import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents a menu containing a list of beverages and available extras.
 */
public class Menu {

    private List<Beverage> menuItems;
    private Set<String> extras;

    /**
     * Constructs a Menu object with the given list of beverages and set of extras.
     *
     * @param menuItems The list of beverages in the menu.
     * @param extras    The set of available extras.
     */
    public Menu(List<Beverage> menuItems, Set<String> extras) {
        this.menuItems = menuItems;
        this.extras = extras;
    }

    /**
     * Finds beverages in the menu that match the given dream beverage.
     *
     * @param dreamBeverage The dream beverage to match against the menu.
     * @return A list of beverages from the menu that match the dream beverage.
     */
    public List<Beverage> findDreamBeverageInMenu(Beverage dreamBeverage) {
        List<Beverage> matches = new ArrayList<>();
        for (Beverage menuItem : menuItems) {
            boolean typeMatch = menuItem.getDreamBeverage().getType().equals(dreamBeverage.getDreamBeverage().getType());
            boolean withinPriceLimit = menuItem.getPrice() >= dreamBeverage.getDreamBeverage().getPriceRange()[0] && menuItem.getPrice() <= dreamBeverage.getDreamBeverage().getPriceRange()[1];
            boolean sugarMatch = dreamBeverage.getDreamBeverage().getSugarPreference() == SugarPreference.DONT_MIND || menuItem.getDreamBeverage().getSugarPreference() == dreamBeverage.getDreamBeverage().getSugarPreference();
            boolean milkMatch = false;
            boolean extraMatch = false;
            boolean tempMatch = false;
            boolean steepMatch = false;
            boolean shotMatch = false;

            if (dreamBeverage instanceof Tea && menuItem instanceof Tea) {
                if (((Tea) dreamBeverage).getSteepingTime() == -1) {
                    steepMatch = true;
                } else steepMatch = ((Tea) menuItem).getSteepingTime() == ((Tea) dreamBeverage).getSteepingTime();
                if (((Tea) dreamBeverage).getTemperature() == -1) {
                    tempMatch = true;
                } else tempMatch = ((Tea) menuItem).getTemperature() == ((Tea) dreamBeverage).getTemperature();
            } else if (dreamBeverage instanceof Coffee && menuItem instanceof Coffee) {
                    shotMatch = ((Coffee) menuItem).getNumberOfShots() == ((Coffee) dreamBeverage).getNumberOfShots();
                if (((Coffee) dreamBeverage).getNumberOfShots() == -1) {
                    shotMatch = true;
                }
            }

            // Matching milk
            if (dreamBeverage.getDreamBeverage().getMilkOptions().isEmpty() || dreamBeverage.getDreamBeverage().getMilkOptions().contains(Milk.NO_MILK)) {
                milkMatch = menuItem.getDreamBeverage().getMilkOptions().isEmpty() || menuItem.getDreamBeverage().getMilkOptions().contains(Milk.NO_MILK);
            } else {
                if (dreamBeverage.getDreamBeverage().getMilkOptions().contains(Milk.I_DONT_MIND)) {
                    milkMatch = true;
                } else milkMatch = menuItem.getDreamBeverage().getMilkOptions().contains(dreamBeverage.getDreamBeverage().getMilkOptions().get(0));
            }



            // Matching at least 1 extra
            if (dreamBeverage.getDreamBeverage().getExtras().isEmpty() || dreamBeverage.getDreamBeverage().getExtras().contains("I don't mind")) {
                extraMatch = true;
            } else {
                for (String extra : dreamBeverage.getDreamBeverage().getExtras()) {
                    for (String menuItemExtra : menuItem.getDreamBeverage().getExtras()) {
                        if (menuItemExtra.equalsIgnoreCase(extra)) {
                            extraMatch = true;
                            break;
                        }
                    }
                }
            }

            // Assuming a match is when all conditions are met
            if (typeMatch && withinPriceLimit && sugarMatch && milkMatch && extraMatch) {
                if (menuItem instanceof Tea) {
                    if (tempMatch && steepMatch) {
                        matches.add(menuItem);
                    }
                }
                if (menuItem instanceof Coffee) {
                    if (shotMatch) {
                        matches.add(menuItem);
                    }
                }
            }
        }
        return matches;
    }

    /**
     * Retrieves the set of available extras in the menu.
     *
     * @return The set of available extras.
     */
    public Set<String> getExtras() {
        return extras;
    }

    /**
     * Sets the set of available extras in the menu.
     *
     * @param extras The set of available extras.
     */
    public void setExtras(Set<String> extras) {
        this.extras = extras;
    }

    /**
     * Retrieves the list of beverages in the menu.
     *
     * @return The list of beverages in the menu.
     */
    public List<Beverage> getMenuItems() {
        return menuItems;
    }

    /**
     * Sets the list of beverages in the menu.
     *
     * @param menuItems The list of beverages in the menu.
     */
    public void setMenuItems(List<Beverage> menuItems) {
        this.menuItems = menuItems;
    }
}