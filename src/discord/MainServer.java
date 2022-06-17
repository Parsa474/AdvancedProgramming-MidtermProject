package discord;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MainServer {

    public final static HashMap<String, Client> clients = readClients();
    private final ServerSocket serverSocket;

    private static HashMap<String, Client> readClients() {
        HashMap<String, Client> clients = new HashMap<>();
        File folder = new File("assets\\users");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null)
            for (File file : listOfFiles) {
                Client newClient = getClient(file);
                if (newClient != null)
                    clients.put(newClient.getUser().getUsername(), newClient);
                else System.out.println("null client was read!");
            }
        return clients;
    }

    private static Client getClient(File file) {
        FileInputStream fileIn = null;
        ObjectInputStream in = null;
        try {
            fileIn = new FileInputStream(file);
            in = new ObjectInputStream(fileIn);
            return (Client) in.readObject();
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
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                new Thread(() -> {

                }).start();
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

    public static void signUpClient(Client newClient) {
        clients.put(newClient.getUser().getUsername(), newClient);
        updateDatabase(newClient);
    }

    public static void updateClientInfo(Client client) {
        MainServer.clients.replace(client.getUser().getUsername(), client);
        updateDatabase(client);
    }

    private static void updateDatabase(Client client) {
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            String path = "assets\\users";
            fileOut = new FileOutputStream(path + "\\" + client.getUser().getUsername().concat(".bin"));
            out = new ObjectOutputStream(fileOut);
            out.writeObject(client);
        } catch (FileNotFoundException e) {
            System.out.println("could not find this file!");
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
