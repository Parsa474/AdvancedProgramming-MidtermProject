package discord;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientController {

    private Model user;
    private final View printer;
    private final MySocket mySocket;
    //private Boolean success;

    public ClientController(Model user, Socket socket) {
        this.user = user;
        mySocket = new MySocket(socket);
        printer = new View();
        //success = false;
    }

    public String toString() {
        return user.toString();
    }

    private void start() throws IOException {
        //listenForModelUpdate();
        outer:
        while (true) {
            printer.printLoggedInMenu();
            int command = MyScanner.getInt(1, 4);
            switch (command) {
                case 1 -> sendFriendRequest();
                case 2 -> {
                    user.setFriendRequests(MainServer.updateServerAndGetFriendRequestsList(user.getUsername()));
                    sendRequestIndex();
                }
                case 3 -> {
                    user.setFriends(MainServer.updateServerAndGetFriendsList(user.getUsername()));
                    printer.printList(user.getFriends());
                }
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
                //synchronized (success) {
                //    success.wait();
                //Thread.sleep(1000);
                if (success) {
                    printer.printSuccessMessage("friend request");
                    break;
                } else {
                    printer.printErrorMessage("friend request");
                }
                //}
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRequestIndex() {
        while (true) {
            printer.printList(user.getFriendRequests());
            printer.printGetMessage("index");
            String input = MyScanner.getLine();
            char[] inputs = input.toCharArray();
            Boolean accept = null;
            try {
                switch (inputs.length) {
                    case 1 -> {
                        if (inputs[0] == '0') return;
                        else printer.printErrorMessage("format");
                    }
                    case 2 -> {
                        int index = Character.getNumericValue(inputs[0]);
                        if (index > 0 && index < user.getFriendRequests().size() + 1) {
                            switch (inputs[1]) {
                                case 'A' -> accept = true;
                                case 'R' -> accept = false;
                                default -> printer.printErrorMessage("format");
                            }
                            if (accept != null) {
                                mySocket.write(new CheckFriendRequestsAction(user.getUsername(), index - 1, accept));
                                if (accept) {
                                    printer.printSuccessMessage("accept");
                                } else {
                                    printer.printSuccessMessage("reject");
                                }
                                user = mySocket.readModel();
                            }
                        } else printer.printErrorMessage("boundary");
                    }
                    default -> printer.printErrorMessage("length");
                }
            } catch (Exception e) {
                printer.printErrorMessage("format");
            }
        }
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
                }
            } else break;
        }
        return false;
    }

    private void signUp() throws IOException, ClassNotFoundException {
        Model newUser = receiveUser();
        if (newUser != null) {
            mySocket.write(new SignUpAction(newUser));
            user = mySocket.readModel();
            printer.printSuccessMessage("signUp");
            start();
        }
    }

    private Model receiveUser() {
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
                if (!MainServer.getUsers().containsKey(input)) {
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
                    case 2 -> clientController.signUp();
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
