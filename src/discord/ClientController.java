package discord;

import signals.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;

public class ClientController {
    // Fields:
    private Model user;
    private final View printer;
    private final MyScanner myScanner;
    private final MySocket mySocket;

    // Constructors:
    public ClientController(Model user, Socket socket) {
        this.user = user;
        mySocket = new MySocket(socket);
        printer = new View();
        myScanner = new MyScanner();
    }

    // Getters:
    public Model getUser() {
        return user;
    }

    public View getPrinter() {
        return printer;
    }

    public MyScanner getMyScanner() {
        return myScanner;
    }

    public MySocket getMySocket() {
        return mySocket;
    }

    // Other Methods:
    public String toString() {
        return user.toString();
    }

    private void start() throws IOException, ClassNotFoundException {
        outer:
        while (true) {
            printer.printLoggedInMenu();
            int command = myScanner.getInt(1, 8);
            // update user from the Main Server
            user = mySocket.sendSignalAndGetResponse(new GetUserFromMainServerAction(user.getUsername()));
            if (user == null) {
                printer.printErrorMessage("Cannot receive data from the Main Server!");
                break;
            }
            switch (command) {
                case 1 -> sendFriendRequest();
                case 2 -> sendRequestIndex();
                case 3 -> privateChat();
                case 4 -> createNewServer();
                case 5 -> enterAServer();
                case 6 -> chaneMyUserInfo();
                case 7 -> {
                    mySocket.write(new LogoutAction());
                    user = null;
                    break outer;
                }
                case 8 -> System.exit(0);
            }
        }
    }

    private void sendFriendRequest() {
        String username;
        try {
            while (true) {
                printer.printGetMessage("req");
                printer.printGoBackMessage();
                username = myScanner.getLine();
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
                Boolean success = mySocket.sendSignalAndGetResponse(new SendFriendRequestAction(user.getUsername(), username));
                if (success != null) {
                    if (success) {
                        printer.printSuccessMessage("friend request");
                    } else {
                        printer.printErrorMessage("already sent");
                    }
                    break;
                } else {
                    printer.printErrorMessage("not found username");
                    printer.printErrorMessage("(Or could not connect to the database)");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRequestIndex() {
        while (true) {
            if (user.getFriendRequests().size() == 0) {
                printer.println("Your friend request list is empty");
                break;
            }
            printer.printList(user.getFriendRequests());
            printer.printGetMessage("index");
            printer.printGoBackMessage();
            String input = myScanner.getLine();
            if ("".equals(input)) {
                return;
            }
            char[] inputs = input.toCharArray();
            Boolean accept = null;
            try {
                if (inputs.length == 2) {
                    int index = Character.getNumericValue(inputs[0]) - 1;
                    switch (inputs[1]) {
                        case 'A' -> accept = true;
                        case 'R' -> accept = false;
                        default -> printer.printErrorMessage("format");
                    }
                    if (accept != null) {
                        Boolean success = mySocket.sendSignalAndGetResponse(new CheckFriendRequestsAction(user.getUsername(), index, accept));
                        if (success != null) {
                            if (success) {
                                if (accept) {
                                    printer.printSuccessMessage("accept");
                                } else {
                                    printer.printSuccessMessage("reject");
                                }
                            } else {
                                printer.printErrorMessage("db");
                            }
                        } else {
                            printer.printErrorMessage("not found username");
                            printer.printErrorMessage("They may have changed their username or deleted their account!");
                        }
                        user.getFriendRequests().remove(index);
                    }
                } else printer.printErrorMessage("length");
            } catch (IndexOutOfBoundsException e) {
                printer.printErrorMessage("boundary");
            } catch (Exception e) {
                printer.printErrorMessage("format");
            }
        }
    }

    private Runnable getPrivateChatListener() {
        return () -> {
            Object inObject;
            while (mySocket.isConnected()) {
                try {
                    inObject = mySocket.read();
                    if (inObject instanceof DBConnectFailSignal) {
                        printer.printErrorMessage("db");
                        synchronized (user.getUsername()) {
                            user.getUsername().notify();    // refer to line 235
                        }
                        break;
                    } else if (inObject instanceof String) {    // The String signals are the messages from the friend
                        printer.println((String) inObject);
                    } else if (inObject instanceof Boolean) {
                        if ((Boolean) inObject) {    // true if seen by the friend immediately
                            printer.println("(seen)");
                        }
                    } else if (inObject instanceof Model) {
                        synchronized (user.getUsername()) {
                            user.getUsername().notify();
                            user = (Model) inObject;
                        }
                        break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    mySocket.closeEverything();
                    break;
                }
            }
            printer.printSuccessMessage("exit");
        };
    }

    private void privateChat() {

        // selecting privateChat
        printer.printList(user.getFriends());
        printer.println("select the chat. enter 0 to go back");
        int friendIndex = myScanner.getInt(0, user.getFriends().size()) - 1;
        if (friendIndex == -1) {
            return;
        }
        String friendName = user.getFriends().get(friendIndex);

        // announce to the MainServer that you're in the private chat of this friend now
        user.getIsInChat().replace(friendName, true);
        if (failUpdateUserOnServer()) {
            printer.printErrorMessage("db");
        }

        //printing previous messages
        printer.printList(user.getPrivateChats().get(friendName));

        // receiving messages
        Thread listener = new Thread(getPrivateChatListener());
        listener.start();

        // sending message
        printer.println("enter \"#exit\" to exit the chat");
        while (true) {
            String message = myScanner.getLine();
            try {
                mySocket.write(new PrivateChatAction(user.getUsername(), message, friendName));
                if (message.equals("#exit")) {
                    break;
                }
            } catch (IOException e) {
                printer.printErrorMessage("IO");
            }
        }
        synchronized (user.getUsername()) {
            try {
                user.getUsername().wait();  // refer to line 170
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // no longer in the private chat of the friend
            user.getIsInChat().replace(friendName, false);
        }
        if (failUpdateUserOnServer()) {
            printer.printErrorMessage("db");
        }
    }

    // returns true if DB could not be reached
    private boolean failUpdateUserOnServer() {
        try {
            boolean DBConnect = mySocket.sendSignalAndGetResponse(new UpdateUserOnMainServerAction(user));
            return !DBConnect;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }

    private void createNewServer() throws IOException, ClassNotFoundException {

        String newServerName;
        do {
            printer.printGetMessage("server's name");
            printer.printGoBackMessage();
            newServerName = myScanner.getLine();
            if ("".equals(newServerName)) {
                return;
            } else if (newServerName.trim().equals("")) {
                printer.println("The name can't be made up of spaces!");
            }
        } while ("".equals(newServerName.trim()));
        newServerName = newServerName.trim();

        // make the new server and add its unicode to the user's servers
        int unicode = mySocket.sendSignalAndGetResponse(new CreateNewServerAction());
        Server newServer = new Server(unicode, newServerName, user.getUsername());
        user.getServers().add(unicode);

        // add the new server to the MainServer and database and update the user on the MainServer
        boolean DBConnect = mySocket.sendSignalAndGetResponse(new AddNewServerToDatabaseAction(newServer));
        if (!DBConnect) {
            printer.printErrorMessage("db");
            return;
        }
        DBConnect = mySocket.sendSignalAndGetResponse(new UpdateUserOnMainServerAction(user));
        if (!DBConnect) {
            printer.printErrorMessage("db");
            return;
        }
        // add some friends to the newly made server if you want
        DBConnect = newServer.addFriendsToServer(this);
        if (!DBConnect) {
            return;
        }

        Server newlyCreatedServer = mySocket.sendSignalAndGetResponse(new GetServerFromMainServerAction(unicode));
        newlyCreatedServer.enter(this);
    }

    private void enterAServer() throws IOException, ClassNotFoundException {
        ArrayList<Server> myServers = new ArrayList<>();
        for (int i = 0; i < user.getServers().size(); i++) {
            int unicode = user.getServers().get(i);
            Server server = mySocket.sendSignalAndGetResponse(new GetServerFromMainServerAction(unicode));
            if (server == null) {
                printer.printErrorMessage("enter server");
                break;
            }
            myServers.add(server);
            printer.println((i + 1) + ". " + server.getServerName());
        }
        if (myServers.size() == 0) {
            printer.println("You're not a member of any server...");
            return;
        }
        printer.println("enter 0 to go back");
        int index = myScanner.getInt(0, user.getServers().size());
        if (index != 0) {
            myServers.get(index - 1).enter(this);
        }
    }

    private void chaneMyUserInfo() throws IOException, ClassNotFoundException {
        printer.println(user.toString());
        printer.printChangeUserMenu();
        int command = myScanner.getInt(1, 5);
        String newField = "";
        SignUpOrChangeInfoAction changeInfoAction = new SignUpOrChangeInfoAction(user.getUsername());
        switch (command) {
            case 1 -> newField = receivePassword(changeInfoAction);
            case 2 -> newField = receiveEmail(changeInfoAction);
            case 3 -> newField = receivePhoneNumber(changeInfoAction);
            case 4 -> changeStatus();
            case 5 -> {
                return;
            }
        }
        if (command == 4) {
            printer.println("The status was changed successfully!");
        } else if (newField != null) {
            printer.println("The field was changed successfully!");
        }
    }

    private void changeStatus() throws IOException, ClassNotFoundException {
        printer.printStatusChangeMenu();
        int status = myScanner.getInt(1, 4);
        switch (status) {
            case 1 -> user.setStatus(Status.Online);
            case 2 -> user.setStatus(Status.Idle);
            case 3 -> user.setStatus(Status.DoNotDisturb);
            case 4 -> user.setStatus(Status.Invisible);
        }
        boolean DBConnect = mySocket.sendSignalAndGetResponse(new UpdateUserOnMainServerAction(user));
        if (!DBConnect) {
            printer.printErrorMessage("db");
        }
    }


    private boolean login() throws IOException, ClassNotFoundException {
        while (user == null) {
            printer.printGoBackMessage();
            printer.printGetMessage("username");
            String username = myScanner.getLine();
            if (!"".equals(username)) {
                printer.printGetMessage("password");
                printer.printCancelMessage();
                String password = myScanner.getLine();
                if ("".equals(password)) return false;
                user = mySocket.sendSignalAndGetResponse(new LoginAction(username, password));
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
            SignUpOrChangeInfoAction signUpAction = new SignUpOrChangeInfoAction();
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
            Model newUser = mySocket.sendSignalAndGetResponse(signUpAction);
            if (newUser != null) {
                printer.printSuccessMessage("signUp");
                user = newUser;
                return true;
            }
        }
        return false;
    }

    private String receiveUsername(SignUpOrChangeInfoAction signUpOrChangeInfoAction) throws IOException, ClassNotFoundException {
        while (true) {
            try {
                printer.printGoBackMessage();
                if (user == null) printer.printGetMessage("username");
                else printer.printGetMessage("new username");
                printer.printConditionMessage("username");
                String username = myScanner.getLine();
                if ("".equals(username)) return null;
                else {
                    signUpOrChangeInfoAction.setUsername(username);
                    Boolean success = mySocket.sendSignalAndGetResponse(signUpOrChangeInfoAction);
                    if (success != null) {
                        if (success) {
                            return username;
                        }
                        throw new InvalidPropertiesFormatException("invalid format was used!");
                    } else printer.printErrorMessage("taken");
                }
            } catch (InvalidPropertiesFormatException e) {
                printer.printErrorMessage("format");
            }
        }
    }

    private String receivePassword(SignUpOrChangeInfoAction signUpOrChangeInfoAction) throws IOException, ClassNotFoundException {
        while (true) {
            try {
                printer.printCancelMessage();
                printer.printGetMessage("password");
                printer.printConditionMessage("password");
                String password = myScanner.getLine();
                signUpOrChangeInfoAction.setPassword(password);
                if (mySocket.sendSignalAndGetResponse(signUpOrChangeInfoAction)) {
                    return password;
                }
                throw new InvalidPropertiesFormatException("invalid format was used!");
            } catch (InvalidPropertiesFormatException e) {
                printer.printErrorMessage("format");
            }
        }
    }

    private String receiveEmail(SignUpOrChangeInfoAction signUpOrChangeInfoAction) throws IOException, ClassNotFoundException {
        while (true) {
            try {
                printer.printCancelMessage();
                printer.printGetMessage("email");
                printer.printConditionMessage("email");
                String email = myScanner.getLine();
                if ("".equals(email)) return null;
                signUpOrChangeInfoAction.setEmail(email);
                if (mySocket.sendSignalAndGetResponse(signUpOrChangeInfoAction)) {
                    return email;
                }
            } catch (InvalidPropertiesFormatException e) {
                printer.printErrorMessage("format");
            }
        }
    }

    private String receivePhoneNumber(SignUpOrChangeInfoAction signUpOrChangeInfoAction) throws IOException, ClassNotFoundException {
        while (true) {
            try {
                printer.printCancelMessage();
                printer.printGetMessage("phone number");
                printer.printConditionMessage("phone number");
                String phoneNumber = myScanner.getLine();
                if ("".equals(phoneNumber)) return null;
                signUpOrChangeInfoAction.setPhoneNumber(phoneNumber);
                if (mySocket.sendSignalAndGetResponse(signUpOrChangeInfoAction)) {
                    return phoneNumber;
                }
                throw new InvalidPropertiesFormatException("invalid format was used!");
            } catch (InvalidPropertiesFormatException e) {
                printer.printErrorMessage("format");
            }
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
                int input = clientController.myScanner.getInt(1, 3);
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
            e.printStackTrace();
            new View().printErrorMessage("main server");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            if (clientController != null) clientController.mySocket.closeEverything();
        }
    }
}
