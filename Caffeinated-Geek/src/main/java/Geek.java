import java.util.Scanner;

public record Geek(String name, String phoneNumber, String customOrder) {

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }


    public String getCustomOrder() {
        return customOrder;
    }
}



