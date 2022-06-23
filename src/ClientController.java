import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientController {

    private final Model user;
    private static View printer;
    private Socket currentSocket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private boolean listen;


    public ClientController(Model user) {
        this.user = user;
        printer = new View();
    }

    private static ClientController login() {
        while (true) {
            printer.printGoBackMessage();
            printer.printGetMessage("username");
            String username = Scanner.getString();
            if ("".equals(username)) return null;
            if (!MainServer.users.containsKey(username)) {
                printer.printErrorMessage("not found username");
            } else {
                printer.printGetMessage("password");
                String password = Scanner.getString();
                if (MainServer.users.get(username).getPassword().equals(password)) {
                    return new ClientController(MainServer.users.get(username));
                } else printer.printErrorMessage("password");
            }
        }
    }

    private boolean connect() {
        try {
            user.setSocket(new Socket("127.0.0.1", 6000));
            System.out.println("done1");
            currentSocket = user.getSocket();
            System.out.println("done2");
//            objectInputStream = new ObjectInputStream(currentSocket.getInputStream());
//            System.out.println("done3");
//            objectOutputStream = new ObjectOutputStream(currentSocket.getOutputStream());
            System.out.println("done4");
            bufferedReader = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(currentSocket.getOutputStream()));
            user.setStatus(Model.Status.Online);
            return true;
        } catch (IOException e) {
            printer.printErrorMessage("main server");
        }
        return false;
    }

    private static void signUp() {
        Model newUser = recieveUser();
        if (newUser == null) return;
        MainServer.signUpUser(newUser);
        printer.printSuccessMessage("signUp");
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
            String input = Scanner.getString();
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
            String input = Scanner.getString();
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
            String input = Scanner.getString();
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
            String input = Scanner.getString();
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

    private void start() throws IOException {
        outer:
        while (true) {
            printer.printLoggedInMenu();
            switch (Scanner.getInt(1, 7)) {
                case 1 -> createNewServer();
                case 4 -> sendFriendRequest();
                case 5 -> addNewFriends();
                case 6 -> chatWithFriends();
                case 7 -> {
                    currentSocket.close(); // it will also close user's socket because they're references to a single object
                    break outer;
                }
            }
        }
    }

    private void createNewServer() {
        printer.printGetMessage("server name");
        String serverName;
        do {
            serverName = Scanner.getString();
        } while ("".equals(serverName.trim()));
    }

    private void sendFriendRequest() {
        printer.printGetMessage("send");
        String friendUsername = Scanner.getString();
        if (user.getFriends().contains(friendUsername)) {
            printer.printErrorMessage("already friend");
            return;
        }
        if (MainServer.users.containsKey(friendUsername)) {
            MainServer.users.get(friendUsername).getFriendRequests().add(user.getUsername());
            MainServer.updateUserInfo(MainServer.users.get(friendUsername));
            printer.printSuccessMessage("friend request");
        } else printer.printErrorMessage("not found username");
    }

    private void addNewFriends() {
        boolean acceptSucceed = false, rejectSucceed = false;
        while (true) {
            if (!acceptSucceed) {
                printer.printGetMessage("add friend");
                printer.printConditionMessage("add friend");
                printer.printList(user.getFriendRequests());
                try {
                    String[] acceptedIndexes = Scanner.getString().split(" ");
                    if (!(acceptedIndexes.length == 1 && acceptedIndexes[0].equals("0"))) {
                        for (String index : acceptedIndexes) {
                            String newFriend = user.getFriendRequests().get(Integer.parseInt(index) - 1);
                            user.getFriends().add(newFriend);
                            MainServer.users.get(newFriend).getFriends().add(user.getUsername());
                            MainServer.updateUserInfo(MainServer.users.get(newFriend));
                            user.getFriendRequests().remove(newFriend);

                            // creating privateChat list of messages
                            user.getPrivateChatMessages().put(newFriend, new ArrayList<Message>());
                            MainServer.users.get(newFriend).getPrivateChatMessages().put(user.getUsername(), new ArrayList<Message>());
                        }
                        printer.printSuccessMessage("accept");
                        acceptSucceed = true;
                    }
                } catch (Exception e) {
                    printer.printErrorMessage("list");
                }
            }
            if (!rejectSucceed) {
                printer.printGetMessage("reject request");
                try {
                    String[] rejectedIndexes = Scanner.getString().split(" ");
                    if (!(rejectedIndexes.length == 1 && rejectedIndexes[0].equals("0"))) {
                        for (String index : rejectedIndexes) {
                            String rejected = user.getFriendRequests().get(Integer.parseInt(index) - 1);
                            user.getFriendRequests().remove(rejected);
                        }
                    }
                    printer.printSuccessMessage("reject");
                    rejectSucceed = true;
                } catch (Exception e) {
                    printer.printErrorMessage("list");
                }
            }
            if (acceptSucceed && rejectSucceed) {
                MainServer.updateUserInfo(user);
                break;
            }
        }
    }

    private Thread listenToMessages(ArrayList<Message> messages) {
        return new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    objectInputStream = new ObjectInputStream(currentSocket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (listen && currentSocket.isConnected()) {
                    try {
                        String receivedMessage = (String) objectInputStream.readObject();
                        printer.println(receivedMessage);
                        messages.add(new Message(receivedMessage, new String[]{user.getUsername()}));
                    } catch (IOException e) {
                        printer.printErrorMessage("IO");
                    } catch (ClassNotFoundException e) {
                        printer.printErrorMessage("ClassNotFoundException");
                    }
                }
            }
        };
    }

    private void chatWithFriends() {
        printer.printList(user.getFriends());
        int chatNum = Scanner.getInt(1, user.getFriends().size());
        String friendName = user.getFriends().get(--chatNum);
        ArrayList<Message> messages = user.getPrivateChatMessages().get(friendName);

        printer.printGoBackMessage();
        // printing previous messages
        for (Message msg : messages) {
            printer.println(msg.getMessage());
        }

        listen = true;
        Thread listeningThread = listenToMessages(messages);
        listeningThread.start();

        try {
            objectOutputStream = new ObjectOutputStream(currentSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String newMessage = "";
        while (true) {
            newMessage = Scanner.getString();
            if (newMessage.equals("\n")) {
                listen = false;
//                listeningThread.stop();
                break;
            }

            Message newMessageObject = new Message(newMessage, new String[]{friendName});

            // adding message to the ArrayList
            messages.add(newMessageObject);

            // sending message
            try {
                objectOutputStream.writeObject(newMessage);
            } catch (IOException e) {
                printer.printErrorMessage("IO");
            }
        }
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (currentSocket != null) {
                currentSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String toString() {
        return user.toString();
    }

    public static void main(String[] args) throws IOException {
        printer = new View();
        outer:
        while (true) {
            printer.printInitialMenu();
            int input = Scanner.getInt(1, 3);
            switch (input) {
                case 1 -> {
                    ClientController loggedInClient = login();
                    if (loggedInClient != null) {
                        if (loggedInClient.connect()) {
                            System.out.println("connected");
                            loggedInClient.start();
                        }
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
