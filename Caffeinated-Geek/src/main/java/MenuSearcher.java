import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

/**
 * The MenuSearcher class provides functionality for searching and interacting with a menu of beverages.
 * It allows users to input their dream beverage preferences and search for matching beverages in the menu.
 * Users can select from a list of matching beverages, provide their information, and place an order.
 */
public class MenuSearcher {

    /**
     * Main method to run the program.
     *
     * @param args command line arguments (not used)
     */

    public static void main(String[] args) throws IOException {
        playMusic("Music.wav");
        Scanner sc = new Scanner(System.in);
        MenuSearcher searcher = new MenuSearcher();
        Menu menu = searcher.loadMenuFromFile("menu.txt");
        searcher.runMenuSearch(sc, menu);
        displayGeek();
        sc.close();



//        Scanner sc = new Scanner(System.in);
//        MenuSearcher m = new MenuSearcher();
//        m.promptDreamBeverage(sc);
    }

    /**
     * Starts the music as the application is opened,
     * Credit: cantina band ~ star wars lofi (feat. Tanoshi)
     *
     * ℗ Aram Audio .
     */

    private static void playMusic(String filepath) {
        try {
            File audioFile = new File(filepath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the audio clip continuously
            audioClip.start(); // Start playing the audio
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Display the Geek logo in a JFrame.
     */

    private static void displayGeek() {
        // Load the image
        ImageIcon imageIcon = new ImageIcon("Logo.png");

        // Create a label with the image icon
        JLabel imageLabel = new JLabel(imageIcon);
        // Add the image label to the frame or panel where you want to display it
        JFrame imageFrame = new JFrame("Cafe Geek");
        imageFrame.setLayout(new BorderLayout());
        imageFrame.add(imageLabel, BorderLayout.CENTER);
        imageFrame.pack();
        imageFrame.setLocationRelativeTo(null);
        imageFrame.setVisible(true);
    }

    /**
     * Triggers the searching of the menu, user will input their dream beverage and this method will find a match for it.
     */
    private void runMenuSearch(Scanner sc, Menu menu) {
        boolean repeatDreamBeveragePrompt = true;
        while (repeatDreamBeveragePrompt) {
            Beverage dreamBeverage = promptDreamBeverage(sc, menu);
            List<Beverage> matches = menu.findDreamBeverageInMenu(dreamBeverage);
            if (matches.isEmpty()) {
                int option = handleNoMatches(sc);
                if (option == 1) {
                    // User selected "Search Again"
                    continue; // Restart the loop to prompt for dream beverage again
                } else {
                    // User selected "No" or closed the dialog, end the loop
                    repeatDreamBeveragePrompt = false;
                }
            } else {
                handleMatches(sc, matches, menu);
                repeatDreamBeveragePrompt = false;
            }
        }
    }

    private int handleNoMatches(Scanner sc) {
        Object[] options = {"Yes", "No"};
        int response = JOptionPane.showOptionDialog(
                null,
                "No matches found. Would you like to search for another order?",
                "No Matches Found",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (response == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(null, "Goodbye!");
        } else if (response == JOptionPane.YES_OPTION) {
            return 1; // User wants to search again
        } else if (response == JOptionPane.CLOSED_OPTION) {
            JOptionPane.showMessageDialog(null, "Invalid input, please try again.");
        }
        return 0; // Default return value
    }

    /**
     * If matches are found, this triggers the user to select an option and then write down their details to be uploaded to userinfo.txt.
     */
    private void handleMatches(Scanner sc, List<Beverage> matches, Menu menu) {
        boolean repeatSearch = true;
        while (repeatSearch) {
            JPanel panel = new JPanel(new GridLayout(matches.size(), 1));
            ButtonGroup buttonGroup = new ButtonGroup();

            for (int i = 0; i < matches.size(); i++) {
                JRadioButton radioButton = new JRadioButton(matches.get(i).getName());
                radioButton.setActionCommand(Integer.toString(i));
                buttonGroup.add(radioButton);
                panel.add(radioButton);
            }

            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            String[] options = {"OK", "Search Again"};
            int option = JOptionPane.showOptionDialog(null, scrollPane, "Select Beverage", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (option == 0) { // User selected "OK"
                int selection = Integer.parseInt(buttonGroup.getSelection().getActionCommand());
                Beverage selectedBeverage = matches.get(selection);

                StringBuilder selectedBeverageMessage = new StringBuilder("Selected beverage:\n\n");
                selectedBeverageMessage.append(selectedBeverage).append("\n\n");
                selectedBeverageMessage.append("Please provide your information:");

                JOptionPane.showMessageDialog(null, selectedBeverageMessage.toString());

                // Prompt user for information and get the result
                Geek user = promptUserInformation(sc, selectedBeverage, selectedBeverage.getDreamBeverage().getMilkOptions().get(0));

                Milk userMilk = selectedBeverage.getDreamBeverage().getMilkOptions().get(0); // Assuming getting the first milk option
                String orderDetails = generateOrderDetails(user, selectedBeverage, userMilk);

                JOptionPane.showMessageDialog(null, orderDetails); // Output order details

                writeUserInfoToFile(user, selectedBeverage, userMilk, user.getCustomOrder());
                repeatSearch = false; // End the loop after successful selection
            } else if (option == 1) { // User selected "Search Again"
                // Prompt the user to select their dream beverage again
                matches = menu.findDreamBeverageInMenu(promptDreamBeverage(sc, menu));
            } else { // User closed the dialog
                JOptionPane.showMessageDialog(null, "Selection canceled.");
                repeatSearch = false; // End the loop if the user cancels selection
            }
        }
    }

    private Geek promptUserInformation(Scanner sc, Beverage selectedBeverage, Milk userMilk) {
        JFrame frame = new JFrame("User Information");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel nameLabel = new JLabel("Enter your full name:");
        JTextField nameField = new JTextField();
        JLabel phoneLabel = new JLabel("Enter your phone number:");
        JTextField phoneField = new JTextField();
        JLabel customOrderLabel = new JLabel("Enter any custom order details:");
        JTextField customOrderField = new JTextField();

        JButton submitButton = new JButton("Submit");
        Geek[] user = new Geek[1];
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String phoneNumber = phoneField.getText();
                String customOrder = customOrderField.getText();
                if (!name.isEmpty() && !phoneNumber.isEmpty()) {
                    frame.dispose();
                    user[0] = new Geek(name, phoneNumber, customOrder);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill in all the fields.");
                }
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(customOrderLabel);
        panel.add(customOrderField);
        panel.add(new JLabel()); // Empty space for alignment
        panel.add(submitButton);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);

        // Wait for the frame to close and then return the user information
        while (user[0] == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return user[0];
    }

    private int promptSelection(Scanner sc, int maxSelection) {
        int selection;
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(
                        null,
                        "Select the beverage you want to order (Enter the number):",
                        "Beverage Selection",
                        JOptionPane.PLAIN_MESSAGE
                );
                if (input == null) {
                    return -1; // User canceled selection
                }
                selection = Integer.parseInt(input);
                if (selection >= 1 && selection <= maxSelection) {
                    break; // Valid selection, exit the loop
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid selection. Please select one of the items listed with its corresponding number");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid order.");
            }
        }
        return selection;
    }

    private Menu loadMenuFromFile(String filePath) throws IOException {
        List<String> content = Files.readAllLines(Path.of(filePath));
        List<Beverage> beverages = new ArrayList<>();
        Set<String> extras = new HashSet<>();
        for (int i = 1; i < content.size(); i++) {
            beverages.add(parseBeverageString(content.get(i), extras));
        }
        return new Menu(beverages, extras);
    }

    private Beverage parseBeverageString(String beverageString, Set<String> extras) {
        String[] parts = beverageString.split(",(?![^\\[]*\\])");
        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        double price = Double.parseDouble(parts[3]);
        SugarPreference sugar = parts[7].equalsIgnoreCase("yes") ? SugarPreference.YES : SugarPreference.NO;
        List<Milk> milkOptions = extractMilkOptions(parts[8]);
        Set<String> beverageExtras = extractExtras(parts[9], extras);
        String description = parts[10].substring(1, parts[10].length() - 1);

        if (type.equalsIgnoreCase(TypeOfBeverage.TEA.name())) {
            int temperature = parseInteger(parts[5]);
            int steepingTime = parseInteger(parts[6]);
            DreamBeverage dreamBeverage = new DreamBeverage(new double[]{0, 0}, sugar, milkOptions, beverageExtras, TypeOfBeverage.TEA);
            return new Tea(dreamBeverage, id, name, description, price, temperature, steepingTime);
        } else if (type.equalsIgnoreCase(TypeOfBeverage.COFFEE.name())) {
            int numberOfShots = parseInteger(parts[4]);
            DreamBeverage dreamBeverage = new DreamBeverage(new double[]{0, 0}, sugar, milkOptions, beverageExtras, TypeOfBeverage.COFFEE);
            return new Coffee(dreamBeverage, id, name, description, price, numberOfShots);
        }
        return null;
    }

    private int parseInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1; // Default value when parsing fails
        }
    }

    private static List<String> extractList(String data) {
        List<String> list = new ArrayList<>();
        data = data.trim();
        String[] items = data.substring(1, data.length() - 1).split(",");
        for (String item : items) {
            list.add(item.trim());
        }
        return list;
    }

    private List<Milk> extractMilkOptions(String milkOptionsString) {
        List<Milk> milkOptions = new ArrayList<>();
        for (String milk : extractList(milkOptionsString)) {
            if (!Objects.equals(milk, "")) {
                Milk milkType = Milk.valueOf(milk.toUpperCase().replace("-", "_"));
                if (Arrays.asList(Milk.values()).contains(milkType)) {
                    milkOptions.add(milkType);
                }
            }
        }
        return milkOptions;
    }

    private static Set<String> extractExtras(String extrasString, Set<String> extras) {
        Set<String> beverageExtras = new HashSet<>();
        for (String extra : extractList(extrasString)) {
            if (!Objects.equals(extra, "")) {
                extras.add(extra.toLowerCase());
                beverageExtras.add(extra);
            }
        }
        return beverageExtras;
    }

//    private void runMenuSearch(Scanner sc, Menu menu) {
//        boolean repeatDreamBeveragePrompt = true;
//        while (repeatDreamBeveragePrompt) {
//            Beverage dreamBeverage = promptDreamBeverage(sc, menu);
//            List<Beverage> matches = menu.findDreamBeverageInMenu(dreamBeverage);
//            if (matches.isEmpty()) {
//                handleNoMatches(sc);
//            } else {
//                handleMatches(sc, matches);
//                repeatDreamBeveragePrompt = false;
//            }
//        }
//    }

    private Beverage promptDreamBeverage(Scanner sc, Menu menu) {
        ImageIcon imageIcon = new ImageIcon("cafGeekLogo.png");
        JOptionPane.showMessageDialog(null, "Enter your dream beverage's details:", null, 0, imageIcon);

        List<Milk> milk;
        double minPrice;
        double maxPrice;
        Set<String> extras;
        double[] priceRange;
        SugarPreference sugar;
        DreamBeverage dreamBeverage;

        String type = String.valueOf(promptType(sc));
//        System.err.println(type);
        if (type != null) {
            switch (type.toUpperCase()) {
                case "TEA":
                    int temperature = promptTemperature(sc);
                    int steepingTime = promptSteepingTime(sc);
                    sugar = promptSugar(sc);
                    milk = promptMilkOption(sc);
                    minPrice = promptMinPrice(sc);
                    maxPrice = promptMaxPrice(sc, minPrice);
                    extras = promptExtras(sc, menu);
                    priceRange = new double[]{minPrice, maxPrice};
                    dreamBeverage = new DreamBeverage(priceRange, sugar, milk, extras, TypeOfBeverage.valueOf(type.toUpperCase()));
                    return new Tea(dreamBeverage, temperature, steepingTime);
                case "COFFEE":
                    int numberOfShots = promptShots(sc);
                    sugar = promptSugar(sc);
                    milk = promptMilkOption(sc);
                    minPrice = promptMinPrice(sc);
                    maxPrice = promptMaxPrice(sc, minPrice);
                    extras = promptExtras(sc, menu);
                    priceRange = new double[]{minPrice, maxPrice};

                    dreamBeverage = new DreamBeverage(priceRange, sugar, milk, extras, TypeOfBeverage.valueOf(type.toUpperCase()));
                    return new Coffee(dreamBeverage, numberOfShots);
            }

        }
        return null;
    }



    private static TypeOfBeverage promptType(Scanner sc) {
        boolean repeatInputPrompt = true;
        TypeOfBeverage beverageType = null;

        while (repeatInputPrompt) {
            Object[] options = {"Tea", "Coffee"};
            String selectedOption = (String) JOptionPane.showInputDialog(
                    null,
                    "Select Beverage Type:",
                    "Beverage Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (selectedOption == null) {
                // User closed the dialog
                repeatInputPrompt = false;
            } else {
                switch (selectedOption) {
                    case "Tea":
                        beverageType = TypeOfBeverage.TEA;
                        break;
                    case "Coffee":
                        beverageType = TypeOfBeverage.COFFEE;
                        break;
                }
                repeatInputPrompt = false;
            }
        }
//        System.err.println("bev: " + beverageType);
        return beverageType;
    }

    private SugarPreference promptSugar(Scanner sc) {
        Object[] options = {"Yes", "No", "I don't mind"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Do you want sugar in your beverage?",
                "Sugar Selection",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case JOptionPane.YES_OPTION:
                return SugarPreference.YES;
            case JOptionPane.NO_OPTION:
                return SugarPreference.NO;
            default:
                return SugarPreference.DONT_MIND;
        }
    }


    private static List<Milk> promptMilkOption(Scanner sc) {
        boolean repeatInputPrompt = true;
        List<Milk> selectedMilks = new ArrayList<>();

        while (repeatInputPrompt) {
            Object[] options = {
                    "WHOLE", "SKIM", "SOY", "ALMOND", "OAT", "FULL_CREAM", "COCONUT", "NO MILK", "I don't mind"
            };
            String selectedOption = (String) JOptionPane.showInputDialog(
                    null,
                    "Select Milk Type:",
                    "Milk Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            try {
                if (selectedOption == null) {
                    // User closed the dialog
                    repeatInputPrompt = false;
                } else {
                    if (selectedOption.equals("I don't mind")) {
                        selectedMilks.add(Milk.I_DONT_MIND);
                    } else {
                        Milk milk = Milk.valueOf(selectedOption.replace(" ", "_"));
                        selectedMilks.add(milk);
                    }
                    JOptionPane.showMessageDialog(null, "You selected: " + selectedOption);
                }
                repeatInputPrompt = false;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please select a valid option.", "Error", JOptionPane.ERROR_MESSAGE);
                repeatInputPrompt = true;
            }
        }

        return selectedMilks;
    }

    private static double promptMinPrice(Scanner sc) {
        boolean repeatInputSelection = true;
        double minPrice = 0; // default initial value

        while (repeatInputSelection) {
            String input = JOptionPane.showInputDialog(null, "Enter the minimum price for the beverage (AUD):");

            try {
                minPrice = Double.parseDouble(input);
                if (minPrice >= 0) { // Ensure minPrice is not negative
                    repeatInputSelection = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Minimum price cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return minPrice;
    }

    private static double promptMaxPrice(Scanner sc, double minPrice) {
        boolean repeatInputSelection = true;
        double maxPrice = minPrice; // Set the default value to minPrice

        while (repeatInputSelection) {
            String input = JOptionPane.showInputDialog(null, "Enter the maximum price for the beverage (AUD):");

            try {
                maxPrice = Double.parseDouble(input);
                if (maxPrice >= minPrice) { // Ensure maxPrice is not lower than minPrice
                    repeatInputSelection = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Maximum price cannot be lower than minimum price.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return maxPrice;
    }

    private static Set<String> promptExtras(Scanner sc, Menu menu) {
        boolean repeatInputSelection = true;
        Set<String> extras = new HashSet<>();

        // Parse menu.txt to extract extras
        Set<String> allExtras = menu.getExtras();

        while (repeatInputSelection) {
            // Add "I don't mind" and "Skip" to the options
            Object[] options = allExtras.toArray();
            Object[] optionsWithSkipAndIDontMind = Arrays.copyOf(options, options.length + 2);
            optionsWithSkipAndIDontMind[optionsWithSkipAndIDontMind.length - 2] = "I don't mind";
            optionsWithSkipAndIDontMind[optionsWithSkipAndIDontMind.length - 1] = "Skip";

            String selectedOption = (String) JOptionPane.showInputDialog(
                    null,
                    "Select Extra Type:",
                    "Extra Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    optionsWithSkipAndIDontMind,
                    optionsWithSkipAndIDontMind[0]
            );

            if (selectedOption == null || selectedOption.equals("Skip")) {
                repeatInputSelection = false;
            } else {
                if (!selectedOption.equals("I don't mind")) {
                    extras.add(selectedOption);
                }
            }
        }

        StringBuilder message = new StringBuilder("You selected the following extras: ");
        for (String extra : extras) {
            message.append(extra).append(", ");
        }
        JOptionPane.showMessageDialog(null, message.toString());
        return extras;
    }

    private static int promptTemperature(Scanner sc) {
        boolean repeatInputSelection = true;
        int temperature = 0;

        // Array of temperature options including "I don't mind"
        String[] temperatureOptions = {
                "80 degrees: For a mellow, gentler taste",
                "85 degrees: For slightly sharper than mellow",
                "90 degrees: Balanced, strong but not too strong",
                "95 degrees: Strong, but not acidic",
                "100 degrees: For a bold, strong flavour",
                "I don't mind"
        };

        while (repeatInputSelection) {
            String selectedOption = (String) JOptionPane.showInputDialog(
                    null,
                    "Select the temperature for the beverage:",
                    "Temperature Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    temperatureOptions,
                    temperatureOptions[0]
            );

            if (selectedOption == null) {
                // User closed the dialog
                repeatInputSelection = false;
            } else if (selectedOption.equals("I don't mind")) {
                temperature = -1; // Set temperature to -1 for "I don't mind" option
                repeatInputSelection = false;
            } else {
                // Extract temperature from the selected option
                temperature = Integer.parseInt(selectedOption.split(" ")[0]);
                repeatInputSelection = false;
            }
        }
        return temperature;
    }

    private static int promptShots(Scanner sc) {
        boolean repeatInputSelection = true;
        int numberOfShots = 0;

        while (repeatInputSelection) {
            String input = JOptionPane.showInputDialog(null, "Enter the number of shots for the beverage or type 'I don't mind':");
            if (input == null || input.equalsIgnoreCase("I don't mind")) {
                numberOfShots = -1; // Set number of shots to -1 for "I don't mind" option or if the user cancels
                repeatInputSelection = false;
            } else {
                try {
                    numberOfShots = Integer.parseInt(input);
                    repeatInputSelection = false;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number of shots or 'I don't mind'.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        return numberOfShots;
    }

    private static int promptSteepingTime(Scanner sc) {
        boolean repeatInputSelection = true;
        int steepingTime = 0;

        // Array of steeping time options including "I don't mind"
        String[] steepingTimeOptions = {
                "I don't mind",
                "Enter preferred steeping time manually"
        };

        while (repeatInputSelection) {
            String selectedOption = (String) JOptionPane.showInputDialog(
                    null,
                    "Select the steeping time for the beverage:",
                    "Steeping Time Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    steepingTimeOptions,
                    steepingTimeOptions[0]
            );

            if (selectedOption == null) {
                // User closed the dialog
                repeatInputSelection = false;
            } else if (selectedOption.equals("Enter preferred steeping time manually")) {
                String input = JOptionPane.showInputDialog(null, "Enter your preferred steeping time for the beverage (in minutes):");
                try {
                    steepingTime = Integer.parseInt(input);
                    repeatInputSelection = false;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid steeping time.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (selectedOption.equals("I don't mind")) {
                steepingTime = -1; // Set steeping time to -1 for "I don't mind" option
                repeatInputSelection = false;
            }
        }

        return steepingTime;
    }

    private String generateOrderDetails(Geek user, Beverage beverage, Milk userMilk) {
        String stringMilk = userMilk.toString();
        String formattedMilk = stringMilk.substring(0, 1).toUpperCase() + stringMilk.substring(1).toLowerCase();
        formattedMilk = formattedMilk.replace('_', ' ');
        StringBuilder builder = new StringBuilder();
        builder.append("Order details:\n");
        builder.append(String.format("       Name: %s\n", user.getName()));
        builder.append(String.format("       Order number: %s\n", user.getPhoneNumber()));
        builder.append(String.format("       Item: %s (%s)\n", beverage.getName(), beverage.getId()));
        builder.append(String.format("       Milk: %s\n", formattedMilk));

        // Append custom order if it exists
        String customOrder = user.getCustomOrder();
        if (customOrder != null && !customOrder.isEmpty()) {
            builder.append(String.format("       Custom Order: %s\n", customOrder));
        }

        return builder.toString();
    }

    private void writeUserInfoToFile(Geek user, Beverage beverage, Milk userMilk, String customOrderDetails) {
        String orderDetails = generateOrderDetails(user, beverage, userMilk);
        Scanner sc = new Scanner(System.in);
        String fullName = user.getName();
        String[] nameParts = fullName.split(" ");
        String firstName = nameParts[0];
        try (FileWriter writer = new FileWriter("userinfo.txt", true)) {
            writer.write(orderDetails + "\n"); // Write standard order details
            writer.write(customOrderDetails + "\n"); // Write custom order details
            JOptionPane.showMessageDialog(null, "Thank you for your patience " + firstName + ", your order has been sent through :)");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "There was an error when processing your order, please try again: " + e.getMessage());
            // Optionally, you can prompt the user for information again here
        }
    }



}
