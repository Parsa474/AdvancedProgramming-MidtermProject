package discord;

import java.io.*;
import java.net.Socket;

public class ClientController {

    private Model user;
    private final View printer;
    private final MySocket mySocket;

    public ClientController(Model user, Socket socket) {
        this.user = user;
        mySocket = new MySocket(socket);
        printer = new View();
    }

    public String toString() {
        return user.toString();
    }

    private void start() throws IOException, ClassNotFoundException {
        outer:
        while (true) {
            printer.printLoggedInMenu();
            int command = MyScanner.getInt(1, 4);
            mySocket.write(new updateRequestAction(user.getUsername()));
            user = mySocket.readModel();
            switch (command) {
                case 1 -> sendFriendRequest();
                case 2 -> sendRequestIndex();
                case 3 -> printer.printList(user.getFriends());
                case 4 -> {
                    mySocket.write(null);
                    user = null;
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

    private void sendRequestIndex() {
        while (true) {
            if (user.getFriendRequests().size() == 0) {
                System.out.println("nothing is here");
                break;
            }
            printer.printList(user.getFriendRequests());
            printer.printGetMessage("index");
            printer.printGoBackMessage();
            String input = MyScanner.getLine();
            if ("".equals(input)) {
                return;
            }
            char[] inputs = input.toCharArray();
            Boolean accept = null;
            try {
                if (inputs.length == 2) {
                    int index = Character.getNumericValue(inputs[0]);
                    if (index > 0 && index < user.getFriendRequests().size() + 1) {
                        switch (inputs[1]) {
                            case 'A' -> accept = true;
                            case 'R' -> accept = false;
                            default -> printer.printErrorMessage("format");
                        }
                        if (accept != null) {
                            mySocket.write(new CheckFriendRequestsAction(user.getUsername(), index - 1, accept));
                            if (mySocket.readBoolean()) {
                                if (accept) {
                                    printer.printSuccessMessage("accept");
                                } else {
                                    printer.printSuccessMessage("reject");
                                }
                            } else {
                                printer.printErrorMessage("not found username");
                            }
                            user.getFriendRequests().remove(index);
                        }
                    } else printer.printErrorMessage("boundary");
                } else printer.printErrorMessage("length");
            } catch (Exception e) {
                printer.printErrorMessage("format");
            }
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String message;
            while (mySocket.getConnectionSocket().isConnected()) {
                try {
                    message = mySocket.readString();
                    System.out.println(message);
                } catch (IOException | ClassNotFoundException e) {
                    mySocket.closeEverything();
                    break;
                }
            }
        }).start();
    }

    private boolean login() throws IOException, ClassNotFoundException {
        while (user == null) {
            printer.printGoBackMessage();
            printer.printGetMessage("username");
            String username = MyScanner.getLine();
            if (!"".equals(username)) {
                printer.printGetMessage("password");
                String password = MyScanner.getLine();
                mySocket.write(new LoginAction(username, password));
                user = mySocket.readModel();
                if (user != null) {
                    printer.printSuccessMessage("login");
                    return true;
                } else printer.printErrorMessage("login");
            } else break;
        }
        return false;
    }

    private boolean signUp() throws IOException, ClassNotFoundException {
        while (user == null) {
            SignUpAction signUpAction = new SignUpAction();
            String username;
            username = receiveUsername(signUpAction);
            if (username == null) return false;
            String password;
            password = receivePassword(signUpAction);
            if (password == null) return false;
            String email;
            email = receiveEmail(signUpAction);
            if (email == null) return false;
            String phoneNumber;
            phoneNumber = receivePhoneNumber(signUpAction);
            if (phoneNumber == null) return false;
            signUpAction.finalizeStage();
            mySocket.write(signUpAction);
            Model newUser = mySocket.readModel();
            if (newUser != null) {
                printer.printSuccessMessage("signUp");
                user = newUser;
                return true;
            }
        }
        return false;
    }

    private String receiveUsername(SignUpAction signUpAction) throws IOException, ClassNotFoundException {
        while (true) {
            printer.printGoBackMessage();
            printer.printGetMessage("username");
            printer.printConditionMessage("username");
            String username = MyScanner.getLine();
            if ("".equals(username)) return null;
            else {
                signUpAction.setUsername(username);
                mySocket.write(signUpAction);
                if (mySocket.readBoolean()) {
                    return username;
                } else printer.printErrorMessage("username");
            }
        }
    }

    private String receivePassword(SignUpAction signUpAction) throws IOException, ClassNotFoundException {
        while (true) {
            printer.printCancelMessage();
            printer.printGetMessage("password");
            printer.printConditionMessage("password");
            String password = MyScanner.getLine();
            signUpAction.setPassword(password);
            mySocket.write(signUpAction);
            if (mySocket.readBoolean()) {
                return password;
            } else {
                printer.printErrorMessage("format");
            }
        }
    }

    private String receiveEmail(SignUpAction signUpAction) throws IOException, ClassNotFoundException {
        while (true) {
            printer.printCancelMessage();
            printer.printGetMessage("email");
            String email = MyScanner.getLine();
            if ("".equals(email)) return null;
            signUpAction.setEmail(email);
            mySocket.write(signUpAction);
            if (mySocket.readBoolean()) {
                return email;
            } else printer.printErrorMessage("email");
        }
    }

    private String receivePhoneNumber(SignUpAction signUpAction) throws IOException, ClassNotFoundException {
        while (true) {
            printer.printCancelMessage();
            printer.printGetMessage("phone number");
            String phoneNumber = MyScanner.getLine();
            if ("".equals(phoneNumber)) return null;
            signUpAction.setPhoneNumber(phoneNumber);
            mySocket.write(signUpAction);
            if (mySocket.readBoolean()) {
                return phoneNumber;
            } else printer.printErrorMessage("illegal character use");
        }
    }

    public static void main(String[] args) {
        ClientController clientController = null;
        try {
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
                    case 2 -> {
                        if (clientController.signUp()) {
                            clientController.start();
                        }
                    }
                    case 3 -> {
                        clientController.mySocket.closeEverything();
                        break outer;
                    }
                }
            }
        } catch (IOException e) {
            new View().printErrorMessage("main server");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            if (clientController != null) clientController.mySocket.closeEverything();
        }
    }
}
