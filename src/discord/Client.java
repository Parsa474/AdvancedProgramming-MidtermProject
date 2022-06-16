package discord;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
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
    public Status status;
    private final LinkedList<String> friendRequests;
    private final LinkedList<String> friends;

    public enum Status {
        Online,
        Idle,
        DoNotDisturb,
        Invisible,
        Offline
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

    private static Client login(Scanner scanner) {
        while (true) {
            System.out.println("(press Enter to go back)");
            System.out.println("enter your username");
            String username = scanner.nextLine();
            if ("".equals(username)) return null;
            if (!MainServer.clients.containsKey(username)) {
                System.out.println("There is no user saved by this username, sign up first or try again");
            } else {
                System.out.println("enter your password");
                String password = scanner.nextLine();
                if (MainServer.clients.get(username).password.equals(password)) {
                    return MainServer.clients.get(username);
                } else System.out.println("wrong password! try again");
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
            System.out.println("could not connect to the main server");
        }
        return false;
    }

    private static void signUp(Scanner scanner) {
        Client newClient = getClient(scanner);
        if (newClient == null) return;
        MainServer.clients.put(newClient.getUsername(), newClient);
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            String path = "assets\\users";
            fileOut = new FileOutputStream(path + "\\" + newClient.username.concat(".bin"));
            out = new ObjectOutputStream(fileOut);
            out.writeObject(newClient);
            System.out.println("The new client has been saved successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("could not find this file!");
        } catch (IOException e) {
            System.out.println("I/O error occurred!");
        } finally {
            handleClosingOutputs(fileOut, out);
        }
    }

    private void updateClientInfo() {
        MainServer.clients.replace(username, this);
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            String path = "assets\\users";
            fileOut = new FileOutputStream(path + "\\" + username.concat(".bin"));
            out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            System.out.println("The new client has been saved successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("could not find this file!");
        } catch (IOException e) {
            System.out.println("I/O error occurred!");
        } finally {
            handleClosingOutputs(fileOut, out);
        }
    }

    private void updateClientInfo(Client target) {
        MainServer.clients.replace(target.username, target);
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            String path = "assets\\users";
            fileOut = new FileOutputStream(path + "\\" + username.concat(".bin"));
            out = new ObjectOutputStream(fileOut);
            out.writeObject(target);
            System.out.println("The new client has been saved successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("could not find this file!");
        } catch (IOException e) {
            System.out.println("I/O error occurred!");
        } finally {
            handleClosingOutputs(fileOut, out);
        }
    }

    private static void handleClosingOutputs(FileOutputStream fileOut, ObjectOutputStream out) {
        if (fileOut != null)
            try {
                fileOut.close();
            } catch (IOException e) {
                System.out.println("I/O error occurred while closing the stream of fileOut!");
            }
        if (out != null)
            try {
                out.close();
            } catch (IOException e) {
                System.out.println("I/O error occurred while closing the stream of out!");
            }

    }

    private static Client getClient(Scanner scanner) {
        String username;
        String password;
        String email;
        String phoneNumber;
        username = getUsername(scanner);
        if (username == null) return null;
        password = getPassword(scanner);
        email = getEmail(scanner);
        phoneNumber = getPhoneNumber(scanner);
        return new Client(username, password, email, phoneNumber);
    }

    private static String getUsername(Scanner scanner) {
        while (true) {
            System.out.println("(press enter to go back)");
            System.out.println("enter your username:");
            String input = scanner.nextLine();
            if ("".equals(input)) return null;
            else {
                if (!MainServer.clients.containsKey(input)) {
                    String regex = "^[A-Za-z0-9]{6,}$";
                    if (isMatched(regex, input)) {
                        return input;
                    } else {
                        System.out.println("invalid format!");
                        System.out.println("should only consist English letters/numbers and be of a minimum length of 6 characters");
                    }
                } else {
                    System.out.println("already taken username! Please choose another username");
                }
            }
        }
    }

    private static String getPassword(Scanner scanner) {
        while (true) {
            System.out.println("enter your password");
            System.out.println("should consist of at least 1 capital letter, 1 small letter, 1 digit, and at least be of a length of 8");
            String input = scanner.nextLine();
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d+]{8,}$";
            if (isMatched(regex, input)) {
                return input;
            } else {
                System.out.println("invalid format!");
            }
        }
    }

    private static String getEmail(Scanner scanner) {
        while (true) {
            System.out.println("enter your email");
            String input = scanner.nextLine();
            try {
                String[] inputs = input.split("@");
                String[] afterAtSign = inputs[1].split("\\.");
                String reg = "^[A-Za-z0-9]*$";
                if (isMatched(reg, inputs[0]) && isMatched(reg, afterAtSign[0]) && isMatched(reg, afterAtSign[1])) {
                    return input;
                } else System.out.println("Do not use illegal characters!");
            } catch (Exception e) {
                System.out.println("this email is invalid (should have an '@' and a finish with a .example)");
            }
        }
    }

    private static String getPhoneNumber(Scanner scanner) {
        while (true) {
            System.out.println("enter your phone number (optional, press Enter if you want to skip)");
            String input = scanner.nextLine();
            if ("".equals(input)) return null;
            String reg = "^[0-9]{11,}$";
            if (isMatched(reg, input)) {
                return input;
            } else System.out.println("Do not use illegal characters!");
        }
    }

    private static boolean isMatched(String regex, String input) {
        Pattern pt = Pattern.compile(regex);
        Matcher mt = pt.matcher(input);
        return mt.matches();
    }

    private void start(Scanner scanner) throws IOException {
        outer:
        while (true) {
            System.out.println("Welcome! What do you want to do?");
            System.out.println("1. create new server");
            System.out.println("2. go to one of my servers");
            System.out.println("3. change my user info");
            System.out.println("4. send a friend request");
            System.out.println("5. check friend request list");
            System.out.println("6. chat with a friend");
            System.out.println("7. log out");
            switch (checkValidity(1, 7, scanner)) {
                case 1 -> createNewServer(scanner);
                case 4 -> sendFriendRequest(scanner);
                case 5 -> addNewFriends(scanner);
                case 6 -> seeFriendsList(scanner);
                case 7 -> {
                    socket.close();
                    break outer;
                }
            }
        }
    }

    private void createNewServer(Scanner scanner) {
        System.out.println("enter the name of the server you want to create");
        String serverName;
        do {
            serverName = scanner.nextLine();
        } while ("".equals(serverName.trim()));
    }

    private void sendFriendRequest(Scanner scanner) {
        System.out.println("Enter the username of the friend you want to send a friend request to");
        String friendUsername = scanner.nextLine();
        if (MainServer.clients.containsKey(friendUsername)) {
            MainServer.clients.get(friendUsername).getFriendRequests().add(username);
            updateClientInfo(MainServer.clients.get(friendUsername));
            System.out.println("The friend request was sent successfully");
        } else System.out.println("A user by this username does not exit!");
    }

    private void addNewFriends(Scanner scanner) {
        while (true) {
            System.out.println("Enter the number of the friend requests you want to accept, enter 0 to select none");
            System.out.println("the numbers should be seperated by 1 space, press Enter when finished");
            for (int i = 0; i < friendRequests.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, friendRequests.get(i));
            }
            try {
                String[] acceptedIndexes = scanner.nextLine().split(" ");
                if (!(acceptedIndexes.length == 1 && acceptedIndexes[0].equals("0"))) {
                    for (String index : acceptedIndexes) {
                        String newFriend = friendRequests.get(Integer.parseInt(index) - 1);
                        friends.add(newFriend);
                        friendRequests.remove(newFriend);
                    }
                    System.out.println("The specified users' requests were accepted");
                } else System.out.println("no request was accepted");
            } catch (Exception e) {
                System.out.println("invalid input, you either entered a non-number character or didn't follow the format");
                System.out.println("try again!");
            }
            System.out.println("follow the same manner for rejecting requests");
            try {
                String[] rejectedIndexes = scanner.nextLine().split(" ");
                if (!(rejectedIndexes.length == 1 && rejectedIndexes[0].equals("0"))) {
                    for (String index : rejectedIndexes) {
                        String rejected = friendRequests.get(Integer.parseInt(index) - 1);
                        friendRequests.remove(rejected);
                    }
                }
                System.out.println("The specified users' requests were rejected");
                MainServer.clients.replace(username, this);
                updateClientInfo();
                break;
            } catch (Exception e) {
                System.out.println("invalid input, you either entered a non-number character or didn't follow the format");
                System.out.println("try again!");
            }
        }
    }

    private void seeFriendsList(Scanner scanner) {
        for (String friend : friends) {
            System.out.println(friend);
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

    private static int checkValidity(int firstNum, int lastNum, Scanner scanner) {
        String input;
        int intInput;
        while (true) {
            input = scanner.nextLine();
            try {
                intInput = Integer.parseInt(input);
                if (intInput < firstNum || intInput > lastNum) System.out.println("invalid input!");
                else break;
            } catch (Exception e) {
                System.out.println("invalid input, Enter a number!");
            }
        }
        return intInput;
    }

    public String toString() {
        return username + " " + password + " " + email + " " + phoneNumber;
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        outer:
        while (true) {
            System.out.println("1. login");
            System.out.println("2. sign up");
            System.out.println("3. exit");
            switch (checkValidity(1, 3, scanner)) {
                case 1 -> {
                    Client loggedInClient = login(scanner);
                    if (loggedInClient != null) {
                        if (loggedInClient.connect())
                            loggedInClient.start(scanner);
                    } else System.out.println("could not login, The main server could be down!");
                }
                case 2 -> signUp(scanner);
                case 3 -> {
                    break outer;
                }
            }
        }
    }
}
