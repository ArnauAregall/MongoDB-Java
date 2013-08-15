import com.arnauaregall.mongodb_java.GridFSDemo;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AppMenu {

    public static void main(String[] args) {
        HashMap<Integer, String> menu = new HashMap<Integer, String>();
        menu.put(1,"Run MongoDB + GridFS Demo");
        menu.put(2,"Insert Tweets to MongoDB");
        menu.put(3,"Exit App");

        Scanner scanner = new Scanner(System.in);

        int selection = 0;
        System.out.println("Welcome to my MongoDB + Java Driver Demo App " +
                "\nBy ArnauAregall  - https://github.com/ArnauAregall\n");

        while (selection == 0) {
            System.out.println("Please, choose what to do :");
            for (Map.Entry<Integer, String> entry : menu.entrySet()) {
                StringBuilder stringBuilder = new StringBuilder()
                        .append("[").append(entry.getKey()).append("] - ").append(entry.getValue());
                System.out.println(stringBuilder.toString());
            }

            String input = scanner.next();
            try {
                selection = Integer.parseInt(input);
            }   catch (Exception e) {
                selection = 0;
            }

            if (menu.containsKey(selection)) {
                System.out.println(menu.get(selection));
                switch (selection) {
                    case 1 :
                        new GridFSDemo();
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }  else {
                selection = 0;
                System.out.println("Please Enter a Valid Option");
            }
        }
        scanner.close();
    }
}
