public class Scanner {

    private final static java.util.Scanner scanner = new java.util.Scanner(System.in);

    public static String getString() {
        return scanner.nextLine();
    }

    public static int getInt(int firstNum, int lastNum) {
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
}
