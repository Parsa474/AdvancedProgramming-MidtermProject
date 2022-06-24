package discord;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientController{

    private final Model user;
    private static View printer;
    private final Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;


    public ClientController(Model user, Socket socket) {
        this.user = user;
        this.socket = socket;
        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything();
        }
        printer = new View();
    }

    private void start() {
        try {
            objectOutputStream.writeObject(user.getUsername());
            outer:
            while (socket.isConnected()) {
                if (user.getStage() == 0) {
                    printer.printLoggedInMenu();
                    user.setStage(MyScanner.getInt(1, 4));
                } else {
                    switch (user.getStage()) {
                        case 1 -> sendFriendRequest();
                        case 2 -> sendListInput();
                        case 4 -> {
                            closeEverything();
                            break outer;
                        }
                    }
                    user.setStage(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFriendRequest() {
        String newFriend;
        while (true) {
            printer.printGetMessage("send");
            printer.printGoBackMessage();
            newFriend = MyScanner.getLine();
            if ("".equals(newFriend)) return;
            if (user.getFriends().contains(newFriend)) {
                printer.printErrorMessage("already friend");
                return;
            }
            if (MainServer.users.containsKey(newFriend)) {
                break;
            } else printer.printErrorMessage("not found username");
        }
        try {
            objectOutputStream.writeObject(newFriend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendListInput() {
        boolean acceptSucceed = false, rejectSucceed = false;
        while (true) {

            if (!acceptSucceed) {
                printer.printGetMessage("friend request list");
                printer.printConditionMessage("friend request list");
                printer.printList(user.getFriendRequests());
                try {
                    String input = MyScanner.getLine();
                    String[] acceptedIndexes = input.split(" ");
                    if (acceptedIndexes.length < 1) throw new Exception();
                    if (checkListInputFormat(acceptedIndexes)) throw new Exception();
                    objectOutputStream.writeObject(input);
                    System.out.println(printer.printSuccessMessage("accept"));
                    acceptSucceed = true;
                } catch (Exception e) {
                    printer.printErrorMessage("list");
                }
            }

            if (!rejectSucceed) {
                try {
                    String input = MyScanner.getLine();
                    String[] rejectedIndexes = input.split(" ");
                    if (rejectedIndexes.length < 1) throw new Exception();
                    if (checkListInputFormat(rejectedIndexes)) throw new Exception();
                    objectOutputStream.writeObject(input);
                    System.out.println(printer.printSuccessMessage("reject"));
                    rejectSucceed = true;
                } catch (Exception e) {
                    printer.printErrorMessage("list");
                }
            }

            if (acceptSucceed && rejectSucceed) {
                try {
                    objectOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    private boolean checkListInputFormat(String[] array) {
        int test;
        boolean output = true;
        try {
            for (String number : array) {
                test = Integer.parseInt(number);
                output = output && (test > 0 && test < array.length);
            }
        } catch (Exception e) {
            return true;
        }
        return output;
    }

    private static ClientController login() {
        while (true) {
            MainServer.updateUsers();
            printer.printGoBackMessage();
            printer.printGetMessage("username");
            String username = MyScanner.getLine();
            if ("".equals(username)) return null;
            if (!MainServer.users.containsKey(username)) {
                printer.printErrorMessage("not found username");
            } else {
                printer.printGetMessage("password");
                String password = MyScanner.getLine();
                if (MainServer.users.get(username).getPassword().equals(password)) {
                    Socket clientControllerSocket;
                    try {
                        clientControllerSocket = new Socket("127.0.0.1", 6000);
                    } catch (IOException e) {
                        printer.printErrorMessage("main server");
                        return null;
                    }
                    return new ClientController(MainServer.users.get(username), clientControllerSocket);
                } else printer.printErrorMessage("password");
            }
        }
    }

    private static void signUp() {
        Model newUser = recieveUser();
        if (newUser == null) return;
        MainServer.signUpUser(newUser);
        System.out.println(printer.printSuccessMessage("signUp"));
    }

    private static Model recieveUser() {
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

    private static String receiveUsername() {
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

    private static String receivePassword() {
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

    private static String receiveEmail() {
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

    private static String receivePhoneNumber() {
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

    public void closeEverything() {
        try {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return user.toString();
    }

    public static void main(String[] args) {
        printer = new View();
        outer:
        while (true) {
            printer.printInitialMenu();
            int input = MyScanner.getInt(1, 3);
            switch (input) {
                case 1 -> {
                    ClientController loggedInClient = login();
                    if (loggedInClient != null) {
                        loggedInClient.start();
                    }
                }
                case 2 -> signUp();
                case 3 -> {
                    break outer;
                }
            }
        }
    }
}
