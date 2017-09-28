package UI;
import java.util.Arrays;
import java.util.List;

public class StudentUI extends UI {

    public static List<String> menuOptions = Arrays.asList("Check Wallet",
        "Buy Artifact",
        "Donate Fundraise",
        "Check Experience",
        "EXIT");

    public static List<String> optionsList = Arrays.asList("1", "2", "3", "4", "0");

    public static void printMenu(){

        for(int i=0; i<menuOptions.size(); i++){
            System.out.format("%s - %s\n", optionsList.get(i), menuOptions.get(i));
        }
    }

    public static String getChoice(){

        String choice;
        do{
            choice = getString("Choose option: ");
        }while (!optionsList.contains(choice));

        return choice;
    }
}