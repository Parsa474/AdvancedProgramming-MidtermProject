package signals;

import mainServer.*;
import discord.Model;

import java.io.IOException;

import static mainServer.ClientHandler.clientHandlers;

public class PrivateChatAction implements Action {
    private final String sender;
    private final String message;
    private final String receiver;

    public PrivateChatAction(String sender, String message, String receiver) {
        this.sender = sender;
        this.message = sender + ": " + message;
        this.receiver = receiver;
    }

    @Override
    public Object act() throws IOException {

        Model senderUser = MainServer.getUsers().get(sender);
        Model receiverUser = MainServer.getUsers().get(receiver);

        if (!message.equals(sender + ": #exit")) {

            // updating database and server
            senderUser.getPrivateChats().get(receiver).add(message);
            receiverUser.getPrivateChats().get(sender).add(message);

            boolean DBConnect = MainServer.updateDatabase(senderUser) && MainServer.updateDatabase(receiverUser);
            if (!DBConnect) {
                return new DBConnectFailSignal();
            }

            // sending message from socket if the receiver is online and in the private chat
            for (ClientHandler c : clientHandlers) {
                Model userOfClientHandler = c.getUser();
                if (userOfClientHandler != null) {
                    if (receiver.equals(userOfClientHandler.getUsername())) {
                        userOfClientHandler = MainServer.getUsers().get(userOfClientHandler.getUsername());
                        if (userOfClientHandler.getIsInChat().get(sender)) {
                            c.getMySocket().write(message); // we can also write "this" object
                            return true;  //seen by the receiver in the moment
                        }
                    }
                }
            }
            return false;  //receiver is not currently in the chat
        } else {
            return senderUser;
        }
    }
}