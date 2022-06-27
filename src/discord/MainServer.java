package discord;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {

    private static Map<String, Model> users = Collections.synchronizedMap(new HashMap<>());
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;

    public MainServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        users = readUsers();
        executorService = Executors.newCachedThreadPool();
    }

    private static void makeDirectory(String path) {
        if (new File(path).exists()) return;
        if (!new File(path).mkdir()) {
            System.out.println("Could not create the " + path + " directory!");
            throw new RuntimeException();
        }
    }

    private static HashMap<String, Model> readUsers() {
        makeDirectory("assets");
        makeDirectory("assets\\users");
        HashMap<String, Model> clients = new HashMap<>();
        File folder = new File("assets\\users");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null)
            for (File file : listOfFiles) {
                Model newUser = readUser(file);
                if (newUser != null)
                    clients.put(newUser.getUsername(), newUser);
                else System.out.println("null user was read!");
            }
        return clients;
    }

    private static Model readUser(File file) {
        FileInputStream fileIn = null;
        ObjectInputStream in = null;
        try {
            fileIn = new FileInputStream(file);
            in = new ObjectInputStream(fileIn);
            return (Model) in.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("file not found while iterating over the users!");
        } catch (IOException e) {
            System.out.println("I/O exception occurred while iterating over the users");
        } catch (ClassNotFoundException e) {
            System.out.println("class not found exception occurred while iterating over the users");
        } finally {
            handleClosingInputs(fileIn, in);
        }
        return null;
    }

    public static Model updateServerAndGetUser(String username) {
        users = readUsers();
        return users.get(username);
    }

    public static Map<String, Model> getUsers() {
        return users;
    }

    private static void handleClosingInputs(FileInputStream fileIn, ObjectInputStream in) {
        if (fileIn != null) try {
            fileIn.close();
        } catch (IOException e) {
            System.out.println("I/O error occurred while closing the stream of fileOut!");
        }
        if (in != null) try {
            in.close();
        } catch (IOException e) {
            System.out.println("I/O error occurred while closing the stream of out!");
        }
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void signUpUser(Model newUser) {
        users.put(newUser.getUsername(), newUser);
        updateDatabase(newUser);
    }

    public static void updateDatabase(Model user) {
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            String path = "assets\\users";
            fileOut = new FileOutputStream(path + "\\" + user.getUsername().concat(".bin"));
            out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find this file!");
        } catch (IOException e) {
            System.out.println("I/O error occurred!");
        } finally {
            handleClosingOutputs(fileOut, out);
        }
    }

    private static void handleClosingOutputs(FileOutputStream fileOut, ObjectOutputStream out) {
        if (fileOut != null) try {
            fileOut.close();
        } catch (IOException e) {
            System.out.println("I/O error occurred while closing the stream of fileOut!");
        }
        if (out != null) try {
            out.close();
        } catch (IOException e) {
            System.out.println("I/O error occurred while closing the stream of out!");
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6000);
        MainServer mainServer = new MainServer(serverSocket);
        mainServer.startServer();
    }
}
