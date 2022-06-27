package discord;

import java.util.ArrayList;
import java.util.LinkedList;

public class View {

    public void printInitialMenu() {
        System.out.println("1. login");
        System.out.println("2. sign up");
        System.out.println("3. exit");
    }

    public void printErrorMessage(String error) {
        switch (error) {
            //case "file not found" -> System.err.println("file not found!");
            //case "IO" -> System.err.println("I/O error occurred!");
            case "yourself" -> System.err.println("You can't send a friend request to yourself!");
            case "already friend" -> System.err.println("This user is already your friend!");
            case "friend request" -> System.err.println("invalid username or friend request already sent!");
            case "not found username" -> System.err.println("A user by this username was not found!");
            case "boundary" -> System.err.println("Out of boundary index used!");
            case "length" -> System.err.println("Invalid input length");
            case "login" -> System.err.println("A username by this password does not exist!");
            case "username" ->
                    System.err.println("You either didn't follow the specified format or this username is already taken!");
            case "format" -> System.err.println("Invalid format!");
            case "email" ->
                    System.err.println("this email is invalid (should have an '@' and a finish with a .example)");
            case "illegal character use" -> System.err.println("Do not use illegal characters!");
            case "main server" -> System.err.println("could not connect to the main server");
            default -> System.err.println(error);
        }
    }

    public void printGoBackMessage() {
        System.out.println("(press enter to go back)");
    }

    public void printCancelMessage() {
        System.out.println("press enter to cancel the process");
    }


    public void printGetMessage(String field) {
        switch (field) {
            case "req" -> System.out.println("Enter the username of the user you want to send a friend request to");
            case "index" ->
                    System.out.println("Enter the index you want to accept followed by 'A' or reject followed by 'R'");
            default -> {
                System.out.println("enter your " + field);
                if (field.equals("phone number")) System.out.println("(optional, simply enter 0 if you want to skip)");
            }
        }
    }

    public void printConditionMessage(String field) {
        switch (field) {
            case "username" ->
                    System.out.println("should only consist English letters/numbers and be of a minimum length of 6 characters");
            case "password" ->
                    System.out.println("should consist of at least 1 capital letter, 1 small letter, 1 digit, and at least be of a length of 8");
        }
    }

    public void printSuccessMessage(String field) {
        switch (field) {
            case "signUp" -> System.out.println("Signed up successfully!");
            case "login" -> System.out.println("logged in successfully");
            case "friend request" -> System.out.println("The request was sent successfully");
            case "accept" -> System.out.println("The specified user's request was accepted");
            case "reject" -> System.out.println("The specified user's request was rejected");
        }
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

    public void println(String text) {
        System.out.println(text);
    }
}
