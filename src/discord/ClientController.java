package discord;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientController implements Serializable {

    private final Model user;
    private static View view;
    transient private Socket socket;
    transient private BufferedReader bufferedReader;
    transient private BufferedWriter bufferedWriter;
    private Status status;
    private final LinkedList<String> friendRequests;
    private final LinkedList<String> friends;

    private enum Status {
        Online, Idle, DoNotDisturb, Invisible, Offline
    }

    public ClientController(String username, String password, String email, String phoneNumber) {
        user = new Model(username, password, email, phoneNumber);
        friendRequests = new LinkedList<>();
        friends = new LinkedList<>();
        status = Status.Offline;
    }

    public Model getUser() {
        return user;
    }

    public LinkedList<String> getFriendRequests() {
        return friendRequests;
    }

    private static ClientController login() {
        while (true) {
            view.printGoBackMessage();
            view.printGetMessage("username");
            String username = Scanner.getString();
            if ("".equals(username)) return null;
            if (!MainServer.clients.containsKey(username)) {
                view.printErrorMessage("not found username");
            } else {
                view.printGetMessage("password");
                String password = Scanner.getString();
                if (MainServer.clients.get(username).user.getPassword().equals(password)) {
                    return MainServer.clients.get(username);
                } else view.printErrorMessage("password");
            }
        }
    }

    private boolean connect() {
        try {
            Socket clientSocket = new Socket("127.0.0.1", 6000);
            socket = clientSocket;
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            status = Status.Online;
            return true;
        } catch (IOException e) {
            view.printErrorMessage("main server");
        }
        return false;
    }

    private static void signUp() {
        ClientController newClient = getClient();
        if (newClient == null) return;
        MainServer.signUpClient(newClient);
    }

    private static ClientController getClient() {
        String username;
        String password;
        String email;
        String phoneNumber;
        username = receiveUsername();
        if (username == null) return null;
        password = receivePassword();
        email = receiveEmail();
        phoneNumber = receivePhoneNumber();
        return new ClientController(username, password, email, phoneNumber);
    }

    private static String receiveUsername() {
        while (true) {
            view.printGoBackMessage();
            view.printGetMessage("username");
            view.printConditionMessage("username");
            String input = Scanner.getString();
            if ("".equals(input)) return null;
            else {
                if (!MainServer.clients.containsKey(input)) {
                    String regex = "^[A-Za-z0-9]{6,}$";
                    if (isMatched(regex, input)) {
                        return input;
                    } else {
                        view.printErrorMessage("format");
                    }
                } else {
                    view.printConditionMessage("takenUsername");
                }
            }
        }
    }

    private static String receivePassword() {
        while (true) {
            view.printGetMessage("password");
            view.printConditionMessage("password");
            String input = Scanner.getString();
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d+]{8,}$";
            if (isMatched(regex, input)) {
                return input;
            } else {
                view.printErrorMessage("format");
            }
        }
    }

    private static String receiveEmail() {
        while (true) {
            view.printGetMessage("email");
            String input = Scanner.getString();
            try {
                String[] inputs = input.split("@");
                String[] afterAtSign = inputs[1].split("\\.");
                String reg = "^[A-Za-z0-9]*$";
                if (isMatched(reg, inputs[0]) && isMatched(reg, afterAtSign[0]) && isMatched(reg, afterAtSign[1])) {
                    return input;
                } else view.printErrorMessage("illegalCharacterUse");
            } catch (Exception e) {
                view.printErrorMessage("email");
            }
        }
    }

    private static String receivePhoneNumber() {
        while (true) {
            view.printGetMessage("number");
            String input = Scanner.getString();
            if ("".equals(input)) return null;
            String reg = "^[0-9]{11,}$";
            if (isMatched(reg, input)) {
                return input;
            } else view.printErrorMessage("illegalCharacterUse");
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
            view.printLoggedInMenu();
            switch (Scanner.getInt(1, 7)) {
                case 1 -> createNewServer();
                case 4 -> sendFriendRequest();
                case 5 -> addNewFriends();
                case 6 -> view.printList(friends);
                case 7 -> {
                    socket.close();
                    break outer;
                }
            }
        }
    }

    private void createNewServer() {
        view.printGetMessage("server name");
        String serverName;
        do {
            serverName = Scanner.getString();
        } while ("".equals(serverName.trim()));
    }

    private void sendFriendRequest() {
        view.printGetMessage("friend request");
        String friendUsername = Scanner.getString();
        if (MainServer.clients.containsKey(friendUsername)) {
            MainServer.clients.get(friendUsername).getFriendRequests().add(user.getUsername());
            MainServer.updateClientInfo(MainServer.clients.get(friendUsername));
            view.printSuccessMessage("friend request");
        } else view.printErrorMessage("not found username");
    }

    private void addNewFriends() {
        boolean acceptSucceed = false, rejectSucceed = false;
        while (true) {
            if (!acceptSucceed) {
                view.printGetMessage("accept");
                view.printConditionMessage("add friend");
                view.printList(friendRequests);
                try {
                    String[] acceptedIndexes = Scanner.getString().split(" ");
                    if (!(acceptedIndexes.length == 1 && acceptedIndexes[0].equals("0"))) {
                        for (String index : acceptedIndexes) {
                            String newFriend = friendRequests.get(Integer.parseInt(index) - 1);
                            friends.add(newFriend);
                            friendRequests.remove(newFriend);
                        }
                        view.printSuccessMessage("accept");
                        acceptSucceed = true;
                    }
                } catch (Exception e) {
                    view.printErrorMessage("list");
                }
            }
            if (!rejectSucceed) {
                view.printGetMessage("reject");
                try {
                    String[] rejectedIndexes = Scanner.getString().split(" ");
                    if (!(rejectedIndexes.length == 1 && rejectedIndexes[0].equals("0"))) {
                        for (String index : rejectedIndexes) {
                            String rejected = friendRequests.get(Integer.parseInt(index) - 1);
                            friendRequests.remove(rejected);
                        }
                    }
                    view.printSuccessMessage("reject");
                    rejectSucceed = true;
                } catch (Exception e) {
                    view.printErrorMessage("list");
                }
            }
            if (acceptSucceed && rejectSucceed) {
                MainServer.updateClientInfo(this);
                break;
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

    public static void main(String[] args) throws IOException {
        outer:
        while (true) {
            view.printInitialMenu();
            int input = Scanner.getInt(1, 3);
            switch (input) {
                case 1 -> {
                    ClientController loggedInClient = login();
                    if (loggedInClient != null) {
                        if (loggedInClient.connect()) loggedInClient.start();
                    } else view.printErrorMessage("login");
                }
                case 2 -> signUp();
                case 3 -> {
                    break outer;
                }
            }
        }
    }
}
