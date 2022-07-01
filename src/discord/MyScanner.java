package discord;

import java.util.ArrayList;
import java.util.Scanner;

public class MyScanner {
    private final Scanner scanner = new Scanner(System.in);

    public String getLine() {
        return scanner.nextLine();
    }

    public int getInt(int firstNum, int lastNum) {
        String input;
        int intInput;
        while (true) {
            input = scanner.nextLine();
            try {
                intInput = Integer.parseInt(input);
                if (intInput < firstNum || intInput > lastNum) System.out.println("invalid input!");
                else break;
            } catch (Exception e) {
                System.out.println("invalid input, Enter a number!");
            }
        }
        return intInput;
    }

    public ArrayList<Integer> getIntList(int max) {
        while (true) {
            try {
                ArrayList<Integer> output = new ArrayList<>();
                String input = scanner.nextLine();
                if ("0".equals(input)) {
                    return output;
                }
                String[] inputs = input.split(" ");
                for (String indexString : inputs) {
                    int index = Integer.parseInt(indexString) - 1;
                    if (index >= 0 && index < max) {
                        output.add(index);
                    } else throw new IndexOutOfBoundsException();
                }
                return output;
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Do not use out of boundary numbers!");
            } catch (NumberFormatException e) {
                System.err.println("Type in numbers!");
            }
        }
    }
}
