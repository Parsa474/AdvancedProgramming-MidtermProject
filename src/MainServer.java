import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {

    public final static HashMap<String, Model> users = readUsers();
    private final ServerSocket serverSocket;

    private static HashMap<String, Model> readUsers() {
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

    public MainServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        System.out.println("Started");
        try {
            System.out.println("Server Created.\nShould wait for a client ... ");
            ExecutorService executorService = Executors.newCachedThreadPool();
            ArrayList<MySocket> connectionSockets = new ArrayList<MySocket>();
            while (!serverSocket.isClosed()) {
                MySocket newConnectionSocket = new MySocket(serverSocket.accept());
                System.out.println("client accepted!");
                connectionSockets.add(newConnectionSocket);
                executorService.execute(new privateChatClientHandler(newConnectionSocket, connectionSockets, users));
                System.out.println("client numbers: " + connectionSockets.size());
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

    public static void updateUserInfo(Model user) {
        MainServer.users.replace(user.getUsername(), user);
        updateDatabase(user);
    }

    private static void updateDatabase(Model user) {
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
        new MainServer(new ServerSocket(6000)).startServer();
    }
}
