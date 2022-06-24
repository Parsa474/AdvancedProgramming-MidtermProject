package discord;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    //public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Model user;
    private View printer;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;


    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            String username = (String) objectInputStream.readObject();
            user = MainServer.users.get(username);
            printer = new View();
            //broadcastMessage(user.getUsername() + " has entered the app!");
        } catch (IOException e) {
            closeEverything();
        } catch (ClassNotFoundException e) {
            System.out.println();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                switch (user.getStage()) {
                    case 1 -> sendFriendRequest();
                    case 2 -> addNewFriends();
                    case 4 -> closeEverything();
                }
            } catch (IOException e) {
                closeEverything();
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendFriendRequest() throws IOException, ClassNotFoundException {
        String friendUsername = (String) objectInputStream.readObject();
        MainServer.users.get(friendUsername).getFriendRequests().add(user.getUsername());
        MainServer.updateDatabase(MainServer.users.get(friendUsername));
        //printer.printSuccessMessage("friend request");
    }

    private void addNewFriends() throws ClassNotFoundException {
        try {
            String[] acceptedIndexes = ((String) objectInputStream.readObject()).split(" ");
            if (!(acceptedIndexes.length == 1 && acceptedIndexes[0].equals("0"))) {
                for (String index : acceptedIndexes) {
                    String newFriend = user.getFriendRequests().get(Integer.parseInt(index) - 1);
                    user.getFriends().add(newFriend);
                    MainServer.users.get(newFriend).getFriends().add(user.getUsername());
                    MainServer.updateDatabase(MainServer.users.get(newFriend));
                    user.getFriendRequests().remove(newFriend);
                }
            }
            String[] rejectedIndexes = ((String) objectInputStream.readObject()).split(" ");
            if (!(rejectedIndexes.length == 1 && rejectedIndexes[0].equals("0"))) {
                for (String index : rejectedIndexes) {
                    String rejected = user.getFriendRequests().get(Integer.parseInt(index) - 1);
                    user.getFriendRequests().remove(rejected);
                }
            }
            MainServer.updateDatabase(user);
        } catch (IOException e) {
            printer.printErrorMessage("IO");
        }
    }

    public void removeThisAndCloseEverything() {
        //clientHandlers.remove(this);
        //broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
        closeEverything();
    }

    public void closeEverything() {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
