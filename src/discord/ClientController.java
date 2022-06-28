package discord;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController {

    // Fields:
    private Model user;
    private final View printer;
    private final MySocket mySocket;

    // Constructors:
    public ClientController(Model user, Socket socket) {
        this.user = user;
        mySocket = new MySocket(socket);
        printer = new View();
    }

    // Getters:
    public Model getUser() {
        return user;
    }

    public View getPrinter() {
        return printer;
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
            int command = MyScanner.getInt(1, 7);
            mySocket.write(new UpdateUserFromMainServerAction(user.getUsername()));
            user = mySocket.read();
            switch (command) {
                case 1 -> sendFriendRequest();
                case 2 -> sendRequestIndex();
                case 3 -> privateChat();
                case 4 -> createNewServer();
                case 5 -> enterAServer();
                case 6 -> chaneMyUserInfo();
                case 7 -> {
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
                boolean success = mySocket.read();
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
                printer.println("Your friend request list is empty");
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
                    int index = Character.getNumericValue(inputs[0]) - 1;
                    switch (inputs[1]) {
                        case 'A' -> accept = true;
                        case 'R' -> accept = false;
                        default -> printer.printErrorMessage("format");
                    }
                    if (accept != null) {
                        mySocket.write(new CheckFriendRequestsAction(user.getUsername(), index, accept));
                        if (mySocket.read()) {
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
                } else printer.printErrorMessage("length");
            } catch (IndexOutOfBoundsException e) {
                printer.printErrorMessage("boundary");
            } catch (Exception e) {
                printer.printErrorMessage("format");
            }
        }
    }

    private Runnable listenForMessage() {
        return new Runnable() {
            @Override
            public void run() {
                Object inObject;
                while (mySocket.getConnectionSocket().isConnected()) {
                    try {
                        inObject = mySocket.read();
                        if (inObject instanceof String) {
                            printer.println((String) inObject);
                        } else if (inObject instanceof Boolean) {
                            if ((Boolean) inObject) { // seen by the friend
                                printer.println("(seen)");
                            }
                        } else if (inObject instanceof Model) {
                            synchronized (user) {  // should it be user or "this"???????
                                user.notify();
                                user = (Model) inObject;
                            }
                            break;
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
//                        mySocket.closeEverything();
                        break;
                    }
                }
                printer.printSuccessMessage("exit");
            }
        };
    }

    private void privateChat() {
        // selecting privateChat
        printer.printList(user.getFriends());
        printer.println("select the chat. enter 0 to go back");
        int friendIndex = MyScanner.getInt(0, user.getFriends().size()) - 1;
        if (friendIndex == -1) {
            return;
        }
        String friendName = user.getFriends().get(friendIndex);
        user.getIsInChat().replace(friendName, true);
        updateUserOnServer();
        //printing previous messages
        printer.printList(user.getPrivateChats().get(friendName));

        // receiving messages
        Thread listener = new Thread(listenForMessage());
        listener.start();

        // sending message
        printer.println("enter \"#exit\" to exit the chat");
        while (true) {
            String message = MyScanner.getLine();
            try {
                mySocket.write(new PrivateChatAction(user.getUsername(), message, friendName));
                if (message.equals("#exit")) {
                    break;
                }
            } catch (IOException e) {
                printer.printErrorMessage("IO");
            }
        }

//        try {
//            Thread.sleep(1000);
//        }catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        synchronized (user) {
            try {
                user.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            user.getIsInChat().replace(friendName, false);
        }
        updateUserOnServer();
    }

    private void updateUserOnServer() {
        try {
            mySocket.write(new UpdateUserOnMainServerAction(user));
            mySocket.read(); // no usage. just for making connection free
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createNewServer() throws IOException, ClassNotFoundException {
        String newServerName;
        do {
            printer.printGetMessage("server's name");
            printer.printGoBackMessage();
            newServerName = MyScanner.getLine();
            if ("".equals(newServerName)) {
                return;
            } else if (newServerName.trim().equals("")) {
                printer.println("The name can't be made up of spaces!");
            }
        } while ("".equals(newServerName.trim()));
        newServerName = newServerName.trim();
        mySocket.write(new CreateNewServerRequestAction());
        int unicode = mySocket.read();
        Server newServer = new Server(unicode, newServerName, user.getUsername());
        user.getServers().add(unicode);
        ArrayList<String> addedFriends = addFriendsToServer(newServer);
        mySocket.write(new AddNewServerToDatabaseAction(newServer));
        mySocket.read();             //no usage
        for (String addedFriend : addedFriends) {
            mySocket.write(new AddFriendToServerAction(unicode, addedFriend));
            mySocket.read();        // no usage
        }
        printer.println(newServerName + " members:");
        printer.printList(addedFriends);
        mySocket.write(new UpdateUserOnMainServerAction(user));
        mySocket.read();            // no usage
    }

    private ArrayList<String> addFriendsToServer(Server newServer) {
        printer.println("Who do you want to add to your server?");
        printer.println("enter 0 to select no one");
        printer.println("enter the indexes seperated by a space!");
        printer.printList(user.getFriends());
        ArrayList<String> addedFriends = new ArrayList<>();
        for (int friendIndex : getIntList(user.getFriends().size())) {
            String friendUsername = user.getFriends().get(friendIndex);
            newServer.addNewMember(friendUsername);
            addedFriends.add(friendUsername);
        }
        return addedFriends;
    }

    public ArrayList<Integer> getIntList(int max) {
        while (true) {
            try {
                ArrayList<Integer> output = new ArrayList<>();
                String input = MyScanner.getLine();
                if ("0".equals(input)) {
                    return output;
                }
                String[] inputs = input.split(" ");
                for (String indexString : inputs) {
                    int index = Integer.parseInt(indexString) - 1;
                    if (index >= 0 && index < max) {
                        output.add(index);
                    } else throw new IndexOutOfBoundsException();
                }
                return output;
            } catch (IndexOutOfBoundsException e) {
                printer.printErrorMessage("boundary");
            } catch (Exception e) {
                printer.printErrorMessage("illegal character use");
            }
        }
    }

    private void enterAServer() throws IOException, ClassNotFoundException {
        ArrayList<Server> myServers = new ArrayList<>();
        for (int i = 0; i < user.getServers().size(); i++) {
            int unicode = user.getServers().get(i);
            mySocket.write(new EnterServerRequestAction(unicode));
            Server server = mySocket.read();
            myServers.add(server);
            printer.println((i + 1) + ". " + server.getServerName());
        }
        printer.println("enter 0 to go back");
        int index = MyScanner.getInt(0, user.getServers().size());
        if (index != 0) {
            myServers.get(index - 1).enter(this);
        }
    }

    private void chaneMyUserInfo() throws IOException, ClassNotFoundException {
        while (true) {
            printer.println(user.toString());
            printer.printChangeUserMenu();
            int command = MyScanner.getInt(1, 6);
            String newField = "";
            SignUpAction changeAFieldAction = new SignUpAction(user.getUsername());
            switch (command) {
                case 1 -> newField = receiveUsername(changeAFieldAction);
                case 2 -> newField = receivePassword(changeAFieldAction);
                case 3 -> newField = receiveEmail(changeAFieldAction);
                case 4 -> newField = receivePhoneNumber(changeAFieldAction);
                case 5 -> changeStatus();
                case 6 -> {
                    return;
                }
            }
            if (command == 5) {
                printer.println("The status was changed successfully!");
            } else if (newField != null) {
                printer.println("The field was changed successfully!");
                String username;
                if (command == 1) {
                    username = newField;
                } else {
                    username = user.getUsername();
                }
                mySocket.write(new UpdateUserFromMainServerAction(username));
                user = mySocket.read();
                break;
            }
        }
    }

    private void changeStatus() throws IOException, ClassNotFoundException {
        printer.printStatusChangeMenu();
        int status = MyScanner.getInt(1, 4);
        switch (status) {
            case 1 -> user.setStatus(Status.Online);
            case 2 -> user.setStatus(Status.Idle);
            case 3 -> user.setStatus(Status.DoNotDisturb);
            case 4 -> user.setStatus(Status.Invisible);
        }
        mySocket.write(new UpdateUserOnMainServerAction(user));
        mySocket.read();
    }


    private boolean login() throws IOException, ClassNotFoundException {
        while (user == null) {
            printer.printGoBackMessage();
            printer.printGetMessage("username");
            String username = MyScanner.getLine();
            if (!"".equals(username)) {
                printer.printGetMessage("password");
                printer.printCancelMessage();
                String password = MyScanner.getLine();
                if ("".equals(password)) return false;
                mySocket.write(new LoginAction(username, password));
                user = mySocket.read();
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
            Model newUser = mySocket.read();
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
            if (user == null) printer.printGetMessage("username");
            else printer.printGetMessage("new username");
            printer.printConditionMessage("username");
            String username = MyScanner.getLine();
            if ("".equals(username)) return null;
            else {
                signUpAction.setUsername(username);
                mySocket.write(signUpAction);
                if (mySocket.read()) {
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
            if (mySocket.read()) {
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
            if (mySocket.read()) {
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
            if (mySocket.read()) {
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
