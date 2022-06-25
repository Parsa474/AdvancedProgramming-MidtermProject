package discord;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Model user;
    private final MySocket mySocket;

    public ClientHandler(Socket socket) {
        mySocket = new MySocket(socket);
        clientHandlers.add(this);
    }

    @Override
    public void run() {
        while (mySocket.getConnectionSocket().isConnected()) {
            try {
                Action action;
                while (user == null) {
                    action = mySocket.readAction();
                    user = (Model) action.act();
                    mySocket.write(user);
                }
                while (mySocket.getConnectionSocket().isConnected()) {
                    action = mySocket.readAction();
                    if (action == null) {
                        mySocket.write(null);
                    } else {
                        mySocket.write(action.act());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                mySocket.closeEverything();
                throw new RuntimeException(e);
            }
        }
    }


    private void sendFriendRequest() throws IOException, ClassNotFoundException {
        String friendUsername = mySocket.readString();
        MainServer.users.get(friendUsername).getFriendRequests().add(user.getUsername());
        MainServer.updateDatabase(MainServer.users.get(friendUsername));
    }

    private void addNewFriends() throws ClassNotFoundException {
        try {
            String[] acceptedIndexes = mySocket.readString().split(" ");
            if (!(acceptedIndexes.length == 1 && acceptedIndexes[0].equals("0"))) {
                for (String index : acceptedIndexes) {
                    String newFriend = user.getFriendRequests().get(Integer.parseInt(index) - 1);
                    user.getFriends().add(newFriend);
                    MainServer.users.get(newFriend).getFriends().add(user.getUsername());
                    MainServer.updateDatabase(MainServer.users.get(newFriend));
                    user.getFriendRequests().remove(newFriend);
                }
            }
            String[] rejectedIndexes = mySocket.readString().split(" ");
            if (!(rejectedIndexes.length == 1 && rejectedIndexes[0].equals("0"))) {
                for (String index : rejectedIndexes) {
                    String rejected = user.getFriendRequests().get(Integer.parseInt(index) - 1);
                    user.getFriendRequests().remove(rejected);
                }
            }
            MainServer.updateDatabase(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeThisAndCloseEverything() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + user.getUsername() + " has left the chat!");
        mySocket.closeEverything();
    }

    public void broadcastMessage(String messageToSend) {
        //for (ClientHandler clientHandler : clientHandlers) {
        try {
            //if (!clientHandler.clientUsername.equals(clientUsername)) {
            //objectOutputStream.writeObject(new Message(messageToSend));
            mySocket.write(messageToSend);
            //}
        } catch (IOException e) {
            removeThisAndCloseEverything();
        }
        //}
    }
}
