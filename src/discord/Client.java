package discord;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client implements Serializable {

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    transient private Socket socket;
    transient private BufferedReader bufferedReader;
    transient private BufferedWriter bufferedWriter;
    private Status status;
    private final LinkedList<String> friendRequests;
    private final LinkedList<String> friends;

    private enum Status {
        Online, Idle, DoNotDisturb, Invisible, Offline
    }

    public Client(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        friendRequests = new LinkedList<>();
        friends = new LinkedList<>();
        status = Status.Offline;
    }

    public String getUsername() {
        return username;
    }

    public LinkedList<String> getFriendRequests() {
        return friendRequests;
    }

    private static Client login() {
        while (true) {
            View.printGoBackMessage();
            View.printGetMessage("username");
            String username = Controller.getString();
            if ("".equals(username)) return null;
            if (!MainServer.clients.containsKey(username)) {
                View.printErrorMessage("not found username");
            } else {
                View.printGetMessage("password");
                String password = Controller.getString();
                if (MainServer.clients.get(username).password.equals(password)) {
                    return MainServer.clients.get(username);
                } else View.printErrorMessage("password");
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
            View.printErrorMessage("main server");
        }
        return false;
    }

    private static void signUp() {
        Client newClient = getClient();
        if (newClient == null) return;
        MainServer.signUpClient(newClient);
    }

    private static Client getClient() {
        String username;
        String password;
        String email;
        String phoneNumber;
        username = receiveUsername();
        if (username == null) return null;
        password = receivePassword();
        email = receiveEmail();
        phoneNumber = receivePhoneNumber();
        return new Client(username, password, email, phoneNumber);
    }

    private static String receiveUsername() {
        while (true) {
            View.printGoBackMessage();
            View.printGetMessage("username");
            View.printConditionMessage("username");
            String input = Controller.getString();
            if ("".equals(input)) return null;
            else {
                if (!MainServer.clients.containsKey(input)) {
                    String regex = "^[A-Za-z0-9]{6,}$";
                    if (isMatched(regex, input)) {
                        return input;
                    } else {
                        View.printErrorMessage("format");
                    }
                } else {
                    View.printConditionMessage("takenUsername");
                }
            }
        }
    }

    private static String receivePassword() {
        while (true) {
            View.printGetMessage("password");
            View.printConditionMessage("password");
            String input = Controller.getString();
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d+]{8,}$";
            if (isMatched(regex, input)) {
                return input;
            } else {
                View.printErrorMessage("format");
            }
        }
    }

    private static String receiveEmail() {
        while (true) {
            View.printGetMessage("email");
            String input = Controller.getString();
            try {
                String[] inputs = input.split("@");
                String[] afterAtSign = inputs[1].split("\\.");
                String reg = "^[A-Za-z0-9]*$";
                if (isMatched(reg, inputs[0]) && isMatched(reg, afterAtSign[0]) && isMatched(reg, afterAtSign[1])) {
                    return input;
                } else View.printErrorMessage("illegalCharacterUse");
            } catch (Exception e) {
                View.printErrorMessage("email");
            }
        }
    }

    private static String receivePhoneNumber() {
        while (true) {
            View.printGetMessage("number");
            String input = Controller.getString();
            if ("".equals(input)) return null;
            String reg = "^[0-9]{11,}$";
            if (isMatched(reg, input)) {
                return input;
            } else View.printErrorMessage("illegalCharacterUse");
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
            View.printLoggedInMenu();
            switch (Controller.getInt(1, 7)) {
                case 1 -> createNewServer();
                case 4 -> sendFriendRequest();
                case 5 -> addNewFriends();
                case 6 -> View.printList(friends);
                case 7 -> {
                    socket.close();
                    break outer;
                }
            }
        }
    }

    private void createNewServer() {
        View.printGetMessage("server name");
        String serverName;
        do {
            serverName = Controller.getString();
        } while ("".equals(serverName.trim()));
    }

    private void sendFriendRequest() {
        View.printGetMessage("friend request");
        String friendUsername = Controller.getString();
        if (MainServer.clients.containsKey(friendUsername)) {
            MainServer.clients.get(friendUsername).getFriendRequests().add(username);
            MainServer.updateClientInfo(MainServer.clients.get(friendUsername));
            View.printSuccessMessage("friend request");
        } else View.printErrorMessage("not found username");
    }

    private void addNewFriends() {
        boolean acceptSucceed = false, rejectSucceed = false;
        while (true) {
            if (!acceptSucceed) {
                View.printGetMessage("accept");
                View.printConditionMessage("add friend");
                View.printList(friendRequests);
                try {
                    String[] acceptedIndexes = Controller.getString().split(" ");
                    if (!(acceptedIndexes.length == 1 && acceptedIndexes[0].equals("0"))) {
                        for (String index : acceptedIndexes) {
                            String newFriend = friendRequests.get(Integer.parseInt(index) - 1);
                            friends.add(newFriend);
                            friendRequests.remove(newFriend);
                        }
                        View.printSuccessMessage("accept");
                        acceptSucceed = true;
                    }
                } catch (Exception e) {
                    View.printErrorMessage("list");
                }
            }
            if (!rejectSucceed) {
                View.printGetMessage("reject");
                try {
                    String[] rejectedIndexes = Controller.getString().split(" ");
                    if (!(rejectedIndexes.length == 1 && rejectedIndexes[0].equals("0"))) {
                        for (String index : rejectedIndexes) {
                            String rejected = friendRequests.get(Integer.parseInt(index) - 1);
                            friendRequests.remove(rejected);
                        }
                    }
                    View.printSuccessMessage("reject");
                    rejectSucceed = true;
                } catch (Exception e) {
                    View.printErrorMessage("list");
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
        return username + " " + password + " " + email + " " + phoneNumber;
    }

    public static void main(String[] args) throws IOException {
        outer:
        while (true) {
            View.printInitialMenu();
            int input = Controller.getInt(1, 3);
            switch (input) {
                case 1 -> {
                    Client loggedInClient = login();
                    if (loggedInClient != null) {
                        if (loggedInClient.connect()) loggedInClient.start();
                    } else View.printErrorMessage("login");
                }
                case 2 -> signUp();
                case 3 -> {
                    break outer;
                }
            }
        }
    }
}
