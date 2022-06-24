package discord;

import java.util.LinkedList;

public class View {

    public void printInitialMenu() {
        System.out.println("1. login");
        System.out.println("2. sign up");
        System.out.println("3. exit");
    }

    public void printErrorMessage(String error) {
        switch (error) {
            case "format" -> System.out.println("Invalid format!");
            case "illegal character use" -> System.out.println("Do not use illegal characters!");
            case "email" ->
                    System.out.println("this email is invalid (should have an '@' and a finish with a .example)");
            case "file not found" -> System.out.println("file not found!");
            case "IO" -> System.out.println("I/O error occurred!");
            case "not found username" -> System.out.println("A user by this username was not found!");
            case "password" -> System.out.println("wrong password! try again");
            case "list" -> {
                System.out.println("invalid input, you either entered a non-number character or didn't follow the format");
                System.out.println("try again!");
            }
            case "main server" -> System.out.println("could not connect to the main server");
            case "already friend" -> System.out.println("This user is already your friend!");
        }
    }

    public void printGoBackMessage() {
        System.out.println("(press enter to go back)");
    }

    public void printGetMessage(String field) {
        switch (field) {
            case "send" -> System.out.println("Enter the username of the user you want to send a friend request to");
            case "friend request list" -> {
                System.out.println("Enter the numbers of the friend requests you want to accept, enter 0 to select none");
                System.out.println("follow the same manner for rejecting requests in a new line");
            }
            default -> {
                System.out.println("enter your " + field);
                if (field.equals("phone number")) System.out.println("(optional, press Enter if you want to skip)");
            }
        }
    }

    public void printConditionMessage(String field) {
        switch (field) {
            case "username" ->
                    System.out.println("should only consist English letters/numbers and be of a minimum length of 6 characters");
            case "taken username" -> System.out.println("already taken username! Please choose another username");
            case "password" ->
                    System.out.println("should consist of at least 1 capital letter, 1 small letter, 1 digit, and at least be of a length of 8");
            case "friend request list" ->
                    System.out.println("the numbers should be seperated by 1 space, press Enter when finished");
        }
    }

    public String printSuccessMessage(String field) {
        switch (field) {
            case "signUp" -> {
                return "Signed up successfully!";
            }
            case "friend request" -> {
                return "The request was sent successfully";
            }
            case "accept" -> {
                return "The specified users' requests were accepted";
            }
            case "reject" -> {
                return "The specified users' requests were rejected";
            }
        }
        return null;
    }

    public void printLoggedInMenu() {
        System.out.println("1. send a friend request");
        System.out.println("2. check friend request list");
        System.out.println("3. chat with a friend");
        System.out.println("4. log out");
    }

    public void printList(LinkedList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, list.get(i));
        }
    }
}