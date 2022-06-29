package discord;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

public class View {

    public void println(String message) {
        System.out.println(message);
    }

    public void printInitialMenu() {
        System.out.println("1. Login");
        System.out.println("2. Sign up");
        System.out.println("3. Exit");
    }

    public void printErrorMessage(String error) {
        switch (error) {
            case "IO" -> System.err.println("I/O error occurred!");
            case "yourself" -> System.err.println("You can't send a friend request to yourself!");
            case "already friend" -> System.err.println("This user is already your friend!");
            case "already sent" -> System.err.println("You have already sent a friend request to this user!");
            case "not found username" -> System.err.println("A user by this username was not found!");
            case "boundary" -> System.err.println("Out of boundary index used!");
            case "length" -> System.err.println("Invalid input length");
            case "login" -> System.err.println("A username by this password does not exist!");
            case "username" ->
                    System.err.println("You either didn't follow the specified format or this username is already taken!");
            case "format" -> System.err.println("Invalid format!");
            case "email" ->
                    System.err.println("This email is invalid (should have an '@' and a finish with a .example)");
            case "illegal character use" -> System.err.println("Do not use illegal characters!");
            case "main server" -> System.err.println("Could not connect to the main server");
            case "change fail" -> System.err.println("Could not change the field, conditions not met");
            case "server name change" ->
                    System.err.println("You don't have the permission to change the server's name!");
            case "db" -> System.err.println("Could not connect to the database!");
            case "unknown" -> System.err.println("An unknown error occurred!");
            case "enter server" -> System.err.println("Could not enter the server!");
            default -> System.err.println(error);
        }
    }

    public void printGoBackMessage() {
        System.out.println("(Press enter to go back)");
    }

    public void printCancelMessage() {
        System.out.println("Press enter to cancel the process");
    }

    public void printGetMessage(String field) {
        switch (field) {
            case "req" -> System.out.println("Enter the username of the user you want to send a friend request to");
            case "index" ->
                    System.out.println("Enter the index you want to accept followed by 'A' or reject followed by 'R'");
            default -> {
                System.out.println("Enter your " + field);
                if (field.equals("phone number")) System.out.println("(Optional, simply enter 0 if you want to skip)");
            }
        }
    }

    public void printConditionMessage(String field) {
        switch (field) {
            case "username" ->
                    System.out.println("Should only consist English letters/numbers and be of a minimum length of 6 characters");
            case "password" ->
                    System.out.println("Should consist of at least 1 capital letter, 1 small letter, 1 digit, and at least be of a length of 8");
        }
    }

    public void printSuccessMessage(String field) {
        switch (field) {
            case "signUp" -> System.out.println("Signed up successfully!");
            case "login" -> System.out.println("Logged in successfully");
            case "friend request" -> System.out.println("The request was sent successfully");
            case "accept" -> System.out.println("The specified user's request was accepted");
            case "reject" -> System.out.println("The specified user's request was rejected");
            case "exit" -> System.out.println("Exited successfully!");
            case "new role" -> System.out.println("The new role was created successfully!");
            case "server name change" -> System.out.println("The server's name was changed successfully!");
        }
    }

    public void printLoggedInMenu() {
        System.out.println("1. Send a friend request");
        System.out.println("2. Check friend request list");
        System.out.println("3. Chat with a friend");
        System.out.println("4. Make a new server");
        System.out.println("5. Go to one of my servers");
        System.out.println("6. Change my user info");
        System.out.println("7. Log out");
    }

    public void printList(ArrayList<String> list) {
        for (String s : list) {
            System.out.println(s);
        }
    }

    public void printList(LinkedList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, list.get(i));
        }
    }

    public void printServerMembersList(Set<String> members) {
        for (String member : members) {
            System.out.println(member);
        }
    }

    public void printTextChannelList(ArrayList<TextChannel> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, list.get(i).getName());
        }
    }

    public void printServerMenu() {
        System.out.println("1. Change server info (make new roles from here)");
        System.out.println("2. Add/remove a member");
        System.out.println("3. Add/remove a text channel");
        System.out.println("4. Enter a text channel");
        System.out.println("5. See all the roles of everyone");
        System.out.println("6. Go back");
    }

    public void printChangeUserMenu() {
        System.out.println("1. Change my username");
        System.out.println("2. Change my password");
        System.out.println("3. Change my email");
        System.out.println("4. Change my phone number");
        System.out.println("5. Change my status");
        System.out.println("6. Go back");
    }

    public void printStatusChangeMenu() {
        System.out.println("1. Online");
        System.out.println("2. Idle");
        System.out.println("3. Do Not Disturb");
        System.out.println("4. Invisible");
    }

    public void printServerChangeInfoMenu() {
        System.out.println("1. Change server's name");
        System.out.println("2. Change a text channel's name");
        System.out.println("3. Create/edit a Role");
        System.out.println("4. Go back");
    }

    public void printAbilityList() {
        System.out.println("1. Can create a new channel");
        System.out.println("2. Can delete a channel");
        System.out.println("3. Can remove a member from the server");
        System.out.println("4. Can limit members from accessing a channel");
        System.out.println("5. Can ban a member from the server");
        System.out.println("6. Can change the server's name");
        System.out.println("7. Can see the history of a chatroom");
        System.out.println("8. Can pin a message");
    }

    public void printRoleEditMenu() {
        System.out.println("1. Creat a new role");
        System.out.println("2. Edit a role");
        System.out.println("3. Go back");
    }
}
