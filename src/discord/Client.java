package discord;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client implements Serializable {

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    private static void launch(Scanner scanner) {
        outer:
        while (true) {
            System.out.println("1. login");
            System.out.println("2. sign up");
            System.out.println("3. exit");

            switch (checkValidity(1, 3, scanner)) {
                case 1 -> {
                    System.out.println(login(scanner));
                }
                case 2 -> signUp(scanner);
                case 3 -> {
                    break outer;
                }
            }
        }
    }

    private static Client login(Scanner scanner) {
        System.out.println("enter your username");
        String username = scanner.nextLine();
        if (!MainServer.clients.containsKey(username)) {
            System.out.println("There is no user saved by this username, sign up first!");
            return null;
        }
        return MainServer.clients.get(username);
    }

    private static void signUp(Scanner scanner) {
        Client newClient = getClient(scanner);
        if (newClient == null) return;
        MainServer.clients.put(newClient.getUsername(), newClient);
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            String path = "assets\\users";
            fileOut = new FileOutputStream(path + "\\" + newClient.username.concat(".bin"));
            out = new ObjectOutputStream(fileOut);
            out.writeObject(newClient);
            System.out.println("The new client has been saved successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("could not find this file!");
        } catch (IOException e) {
            System.out.println("I/O error occurred!");
        } finally {
            handleClosingOutputs(fileOut, out);
        }
    }

    private static void handleClosingOutputs(FileOutputStream fileOut, ObjectOutputStream out) {
        if (fileOut != null)
            try {
                fileOut.close();
            } catch (IOException e) {
                System.out.println("I/O error occurred while closing the stream of fileOut!");
            }
        if (out != null)
            try {
                out.close();
            } catch (IOException e) {
                System.out.println("I/O error occurred while closing the stream of out!");
            }

    }

    private static Client getClient(Scanner scanner) {
        String username;
        String password;
        String email;
        String phoneNumber;
        username = getUsername(scanner);
        if (username == null) return null;
        password = getPassword(scanner);
        email = getEmail(scanner);
        phoneNumber = getPhoneNumber(scanner);
        return new Client(username, password, email, phoneNumber);
    }

    private static String getUsername(Scanner scanner) {
        while (true) {
            System.out.println("(press enter to go back)");
            System.out.println("enter your username:");
            String input = scanner.nextLine();
            if ("".equals(input)) return null;
            else {
                if (!MainServer.clients.containsKey(input)) {
                    String regex = "^[A-Za-z0-9]{6,}$";
                    if (isMatched(regex, input)) {
                        return input;
                    } else {
                        System.out.println("invalid format!");
                        System.out.println("should only consist English letters/numbers and be of a minimum length of 6 characters");
                    }
                } else {
                    System.out.println("already taken username! Please choose another username");
                }
            }
        }
    }

    private static String getPassword(Scanner scanner) {
        while (true) {
            System.out.println("enter your password");
            System.out.println("should consist of at least 1 capital letter, 1 small letter, 1 digit, and at least be of a length of 8");
            String input = scanner.nextLine();
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d+]{8,}$";
            if (isMatched(regex, input)) {
                return input;
            } else {
                System.out.println("invalid format!");
            }
        }
    }

    private static String getEmail(Scanner scanner) {
        while (true) {
            System.out.println("enter your email");
            String input = scanner.nextLine();
            try {
                String[] inputs = input.split("@");
                String[] afterAtSign = inputs[1].split("\\.");
                String reg = "^[A-Za-z0-9]*$";
                if (isMatched(reg, inputs[0]) && isMatched(reg, afterAtSign[0]) && isMatched(reg, afterAtSign[1])) {
                    return input;
                } else System.out.println("Do not use illegal characters!");
            } catch (Exception e) {
                System.out.println("this email is invalid (should have an '@' and a finish with a .example)");
            }
        }
    }

    private static String getPhoneNumber(Scanner scanner) {
        while (true) {
            System.out.println("enter your phone number (optional, press Enter if you want to skip)");
            String input = scanner.nextLine();
            if ("".equals(input)) return null;
            String reg = "^[0-9]{11,}$";
            if (isMatched(reg, input)) {
                return input;
            } else System.out.println("Do not use illegal characters!");
        }
    }

    private static boolean isMatched(String regex, String input) {
        Pattern pt = Pattern.compile(regex);
        Matcher mt = pt.matcher(input);
        return mt.matches();
    }

    private static int checkValidity(int firstNum, int lastNum, Scanner scanner) {
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

    public String toString() {
        return username + " " + password + " " + email + " " + phoneNumber;
    }

    public static void main(String[] args) {
        launch(new Scanner(System.in));
    }
}
