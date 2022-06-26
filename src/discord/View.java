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
            case "format" -> System.out.println("Invalid format!");
            case "illegal character use" -> System.out.println("Do not use illegal characters!");
            case "email" ->
                    System.out.println("this email is invalid (should have an '@' and a finish with a .example)");
            case "file not found" -> System.out.println("file not found!");
            case "IO" -> System.out.println("I/O error occurred!");
            case "not found username" -> System.out.println("A user by this username was not found!");
            case "login" -> System.out.println("A username by this password does not exist!");
            case "list" -> {
                System.out.println("invalid input, you either entered a non-number character or didn't follow the format");
                System.out.println("try again!");
            }
            case "main server" -> System.out.println("could not connect to the main server");
            case "already friend" -> System.out.println("This user is already your friend!");
            case "yourself" -> System.out.println("You can't send a friend request to yourself!");
            case "friend request" -> System.out.println("invalid username or friend request already sent!");
            case "boundary" -> System.out.println("Out of boundary index used!");
            case "repeat" -> System.out.println("You can't reject an already accepted request!");
            case "length" -> System.out.println("Invalid input length");
            default -> System.out.println(error);
        }
    }

    public void printGoBackMessage() {
        System.out.println("(press enter to go back)");
    }

    public void printGetMessage(String field) {
        switch (field) {
            case "req" -> System.out.println("Enter the username of the user you want to send a friend request to");
            case "index" -> {
                System.out.println("Enter the index you want to accept followed by 'A' or reject followed by 'R'");
                System.out.println("Enter 0 to go back");
            }
            case "accept" ->
                    System.out.println("Enter the numbers of the friend requests you want to accept, enter 0 to select none");
            case "reject" -> System.out.println("follow the same manner for rejecting requests in a new line");
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
