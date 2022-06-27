package discord;

import java.io.IOException;

import static discord.ClientHandler.clientHandlers;

public class ChatAction extends Action{
    // Fields:
    private String sender;
    private String message;
    private String receiver;

    // Constructors:
    public ChatAction(String sender, String message, String receiver) {
        this.sender = sender;
        this.message = sender + ": " + message;
        this.receiver = receiver;
    }

    // Methods:
    // Getter Methods:
    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    // Setter Methods:
    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    // Other Methods:
    @Override
    public Object act() throws IOException {
        // updating database and server
        Model senderUser = MainServer.getUsers().get(sender);
        senderUser.getPrivateChats().get(receiver).add(message); // should be checked !!!
        Model receiverUser = MainServer.getUsers().get(receiver);
        receiverUser.getPrivateChats().get(sender).add(message);
        MainServer.updateDatabase(senderUser);
        MainServer.updateDatabase(receiverUser);

        // sending message from socket if the receiver is online and in the private chat
        for (ClientHandler c : clientHandlers) {
            Model userOfClientHandler = c.getUser();
            if (userOfClientHandler != null) {
                if (receiver.equals(userOfClientHandler.getUsername())) {
                    userOfClientHandler = MainServer.getUsers().get(userOfClientHandler.getUsername());  // testtttttttt
                    if (userOfClientHandler.getIsInChat().get(sender)) {
                        c.getMySocket().write(message); // we can also write "this" object
                        return true;  //seen by the receiver in the moment
                    }
                }
            }
        }
        return false;  //receiver is not currently in the chat
    }
}
