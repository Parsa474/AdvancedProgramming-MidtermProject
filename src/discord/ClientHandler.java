package discord;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientHandler implements Runnable {

    public static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    private Model user;
    private final MySocket mySocket;

    public ClientHandler(Socket socket) {
        mySocket = new MySocket(socket);
        clientHandlers.add(this);
    }

    public Model getUser() {
        return user;
    }

    public MySocket getMySocket() {
        return mySocket;
    }

    @Override
    public void run() {
        //outer:
        while (true) {
            try {
                Action action;
                while (user == null) {
                    action = mySocket.readAction();
                    if (action instanceof LoginAction || (action instanceof SignUpAction && ((SignUpAction) action).getStage() == 5)) {
                        user = (Model) action.act();
                        if (user != null) {
                            user.setStatus(Model.Status.Online);
                        }
                        mySocket.write(user);
                    } else {
                        mySocket.write(action.act());
                    }
                }
                while (true) {
                    action = mySocket.readAction();
                    if (action == null) {       // when logging out
                        user = null;
                        break;
                    } else if (action instanceof SignUpAction && ((SignUpAction) action).getSubStage() == 1) {  //when changing username
                        mySocket.write(action.act());
                        user = MainServer.getUsers().get(((SignUpAction) action).getNewUsername());
                        break;
                    } else {
                        mySocket.write(action.act());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                clientHandlers.remove(this);
                mySocket.closeEverything();
                if (user != null)
                    System.out.println("clientHandler of " + user.getUsername() + " got removed");
                else
                    System.out.println("clientHandler of a not logged in client got removed");
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
                if (!clientHandler.user.getUsername().equals(user.getUsername())) {
                    mySocket.write(messageToSend);
                    mySocket.write(messageToSend);
                }
            } catch (IOException e) {
                removeThisAndCloseEverything();
            }
        }
    }*/
}
