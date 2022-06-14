package discord;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MainServer {

    public final static HashMap<String, Client> clients = readClients();
    private ServerSocket serverSocket;

    private static HashMap<String, Client> readClients() {
        HashMap<String, Client> clients = new HashMap<>();
        File folder = new File("assets\\users");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null)
            for (File file : listOfFiles) {
                Client newClient = getClient(file);
                if (newClient != null)
                    clients.put(newClient.getUsername(), newClient);
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
        if (fileIn != null)
            try {
                fileIn.close();
            } catch (IOException e) {
                System.out.println("I/O error occurred while closing the stream of fileOut!");
            }
        if (in != null)
            try {
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

    public static void main(String[] args) throws IOException {
        new MainServer(new ServerSocket(6000)).startServer();
    }
}
