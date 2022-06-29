package mainServer;

import discord.*;
import actions.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientHandler implements Runnable {

    // Fields:
    public static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    private Model user;
    private final MySocket mySocket;

    // Constructors:
    public ClientHandler(Socket socket) {
        mySocket = new MySocket(socket);
        clientHandlers.add(this);
    }

    // Getters:
    public Model getUser() {
        return user;
    }

    public MySocket getMySocket() {
        return mySocket;
    }

    // Methods:
    @Override
    public void run() {
        while (true) {
            try {

                Action action;
                // the first while loop is for signing up or logging in
                while (user == null) {
                    action = mySocket.read();
                    if (action instanceof LoginAction || (action instanceof SignUpOrChangeInfoAction && ((SignUpOrChangeInfoAction) action).getStage() == 5)) {
                        user = (Model) action.act();
                        if (user != null) {
                            user.setStatus(Status.Online);
                        }
                        mySocket.write(user);
                    } else {
                        mySocket.write(action.act());
                    }
                }

                // the second while loop is for any other action after logging in or signing up
                while (true) {
                    action = mySocket.read();
                    // only when logging out the action is null
                    if (action instanceof LogoutAction) {
                        user = null;
                        break;
                        // next condition is met when changing username
                    } else if (action instanceof SignUpOrChangeInfoAction && ((SignUpOrChangeInfoAction) action).getSubStage() == 1) {
                        mySocket.write(action.act());
                        user = MainServer.getUsers().get(((SignUpOrChangeInfoAction) action).getNewUsername());
                        break;
                    } else {
                        // for any other action besides logging out or changing username
                        mySocket.write(action.act());
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                clientHandlers.remove(this);
                mySocket.closeEverything();
                if (user != null) {
                    System.out.println("clientHandler of " + user.getUsername() + " got removed");
                } else {
                    System.out.println("clientHandler of a not logged in client got removed");
                }
                break;
            }
        }
    }
}
