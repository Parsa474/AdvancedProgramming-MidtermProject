package discord;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientController {

    private Model user;
    private final View printer;
    private final MySocket mySocket;


    public ClientController(Model user, Socket socket) {
        this.user = user;
        mySocket = new MySocket(socket);
        printer = new View();
    }

    public void setUser(Model user) {
        this.user = user;
    }

    public String toString() {
        return user.toString();
    }

    private void start() throws IOException {
        outer:
        while (true) {
            printer.printLoggedInMenu();
            int command = MyScanner.getInt(1, 4);
            switch (command) {
                case 1 -> sendFriendRequest();
                case 2 -> sendRequestsIndexes();
                case 3 -> printer.printList(user.getFriends());
                case 4 -> {
                    mySocket.write(null);
                    break outer;
                }
            }
        }
    }

    private void sendFriendRequest() {
        String username;
        try {
            while (true) {
                printer.printGetMessage("req");
                printer.printGoBackMessage();
                username = MyScanner.getLine();
                if ("".equals(username)) {
                    return;
                }
                if (user.getUsername().equals(username)) {
                    printer.printErrorMessage("yourself");
                    return;
                }
                if (user.getFriends().contains(username)) {
                    printer.printErrorMessage("already friend");
                    return;
                }
                mySocket.write(new FriendRequestAction(user.getUsername(), username));
                boolean success = mySocket.readBoolean();
                if (success) {
                    printer.printSuccessMessage("friend request");
                    break;
                } else {
                    printer.printErrorMessage("friend request");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRequestsIndexes() throws IOException {
        boolean acceptSucceed = false, rejectSucceed = false;
        ArrayList<Integer> acceptedIndexes = new ArrayList<>();
        ArrayList<Integer> rejectedIndexes = new ArrayList<>();
        int count = user.getFriends().size();
        do {
            printer.printGetMessage("friend request list");
            printer.printConditionMessage("friend request list");
            printer.printList(user.getFriendRequests());
            if (!acceptSucceed) {
                try {
                    String input = MyScanner.getLine();
                    String[] accepted = input.split(" ");
                    for (String index : accepted) {
                        acceptedIndexes.add(Integer.parseInt(index));
                    }
                    if (acceptedIndexes.size() == 1 && acceptedIndexes.get(0) == 0) {
                        acceptSucceed = true;
                    } else {
                        if (checkListInputFormat(acceptedIndexes, count)) {
                            printer.printSuccessMessage("accept");
                            acceptSucceed = true;
                        } else {
                            printer.printErrorMessage("index");
                        }
                    }
                } catch (Exception e) {
                    printer.printErrorMessage("list");
                }
            }
            if (!rejectSucceed) {
                try {
                    String input = MyScanner.getLine();
                    String[] rejected = input.split(" ");
                    for (String index : rejected) {
                        rejectedIndexes.add(Integer.parseInt(index));
                    }
                    if (rejectedIndexes.size() == 1 && rejectedIndexes.get(0) == 0) {
                        rejectSucceed = true;
                    } else {
                        if (checkListInputFormat(rejectedIndexes, count)) {
                            printer.printSuccessMessage("reject");
                            rejectSucceed = true;
                        } else {
                            printer.printErrorMessage("index");
                        }
                    }
                } catch (Exception e) {
                    printer.printErrorMessage("list");
                }
            }
        } while (!acceptSucceed || !rejectSucceed);
        mySocket.write(new CheckFriendRequestsAction(user, acceptedIndexes, rejectedIndexes));
    }

    private boolean checkListInputFormat(ArrayList<Integer> indexes, int max) {
        boolean output = true;
        for (Integer index : indexes) {
            output = output && (index > 0 && index < max);
        }
        return output;
    }

    public void listenForMessage() {
        new Thread(() -> {
            String successMessage;
            while (mySocket.getConnectionSocket().isConnected()) {
                try {
                    successMessage = mySocket.readString();
                    System.out.println(successMessage);
                } catch (IOException | ClassNotFoundException e) {
                    mySocket.closeEverything();
                    break;
                }
            }
        }).start();
    }

    private boolean login() throws IOException, ClassNotFoundException {
        while (true) {
            printer.printGoBackMessage();
            printer.printGetMessage("username");
            String username = MyScanner.getLine();
            if ("".equals(username)) {
                return false;
            }
            printer.printGetMessage("password");
            String password = MyScanner.getLine();
            mySocket.write(new LoginAction(username, password));
            Model user = mySocket.readModel();
            if (user != null) {
                this.user = user;
                break;
            } else printer.printErrorMessage("login");
        }
        printer.printSuccessMessage("login");
        return true;
    }

    private void signUp() throws IOException, ClassNotFoundException {
        setUser(recieveUser());
        mySocket.write(new SignupAction(user));
        if (mySocket.readBoolean()) {
            printer.printSuccessMessage("signUp");
            start();
        }
    }

    private Model recieveUser() {
        String username;
        String password;
        String email;
        String phoneNumber;
        username = receiveUsername();
        if (username == null) return null;
        password = receivePassword();
        email = receiveEmail();
        phoneNumber = receivePhoneNumber();
        return new Model(username, password, email, phoneNumber);
    }

    private String receiveUsername() {
        while (true) {
            printer.printGoBackMessage();
            printer.printGetMessage("username");
            printer.printConditionMessage("username");
            String input = MyScanner.getLine();
            if ("".equals(input)) return null;
            else {
                if (!MainServer.users.containsKey(input)) {
                    String regex = "^[A-Za-z0-9]{6,}$";
                    if (isMatched(regex, input)) {
                        return input;
                    } else {
                        printer.printErrorMessage("format");
                    }
                } else {
                    printer.printConditionMessage("taken username");
                }
            }
        }
    }

    private String receivePassword() {
        while (true) {
            printer.printGetMessage("password");
            printer.printConditionMessage("password");
            String input = MyScanner.getLine();
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d+]{8,}$";
            if (isMatched(regex, input)) {
                return input;
            } else {
                printer.printErrorMessage("format");
            }
        }
    }

    private String receiveEmail() {
        while (true) {
            printer.printGetMessage("email");
            String input = MyScanner.getLine();
            try {
                String[] inputs = input.split("@");
                String[] afterAtSign = inputs[1].split("\\.");
                String reg = "^[A-Za-z0-9]*$";
                if (isMatched(reg, inputs[0]) && isMatched(reg, afterAtSign[0]) && isMatched(reg, afterAtSign[1])) {
                    return input;
                } else printer.printErrorMessage("illegal character use");
            } catch (Exception e) {
                printer.printErrorMessage("email");
            }
        }
    }

    private String receivePhoneNumber() {
        while (true) {
            printer.printGetMessage("phone number");
            String input = MyScanner.getLine();
            if ("".equals(input)) return null;
            String reg = "^[0-9]{11,}$";
            if (isMatched(reg, input)) {
                return input;
            } else printer.printErrorMessage("illegal character use");
        }
    }

    private static boolean isMatched(String regex, String input) {
        Pattern pt = Pattern.compile(regex);
        Matcher mt = pt.matcher(input);
        return mt.matches();
    }

    public static void main(String[] args) {
        try {
            ClientController clientController;
            Socket clientControllerSocket = new Socket("127.0.0.1", 6000);
            clientController = new ClientController(null, clientControllerSocket);
            outer:
            while (true) {
                clientController.printer.printInitialMenu();
                int input = MyScanner.getInt(1, 3);
                switch (input) {
                    case 1 -> {
                        if (clientController.login()) {
                            clientController.start();
                        }
                    }
                    case 2 -> clientController.signUp();
                    case 3 -> {
                        break outer;
                    }
                }
            }
        } catch (
                IOException e) {
            new View().printErrorMessage("main server");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
