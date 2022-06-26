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
        //outer:
        while (true) {
            try {
                Action action;
                while (user == null) {
                    action = mySocket.readAction();
                    user = (Model) action.act();
                    mySocket.write(user);
                }
                while (true) {
                    action = mySocket.readAction();
                    // for logging out
                    if (action != null) {
                        mySocket.write(action.act());
                    } else {
                        user = null;
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                clientHandlers.remove(this);
                mySocket.closeEverything();
                break;
            }
        }
    }

    /*public void removeThisAndCloseEverything() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + user.getUsername() + " has left the chat!");
        mySocket.closeEverything();
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
        try {
            if (!clientHandler.clientUsername.equals(clientUsername)) {
            objectOutputStream.writeObject(new Message(messageToSend));
            mySocket.write(messageToSend);
            }
        } catch (IOException e) {
            removeThisAndCloseEverything();
        }
        }
    }*/
}
