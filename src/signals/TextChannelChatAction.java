package signals;

import discord.Model;
import discord.TextChannel;
import mainServer.ClientHandler;
import mainServer.MainServer;

import java.io.IOException;
import java.util.ArrayList;

import static mainServer.ClientHandler.clientHandlers;

public class TextChannelChatAction implements Action {
    private final String sender;
    private final String message;
    private final int serverUnicode;
    private final int index;
    private final ArrayList<String> receivers;  // all the members of the textChannel except the sender

    public TextChannelChatAction(String sender, String message, int serverUnicode, int index, ArrayList<String> receivers) {
        this.sender = sender;
        this.message = sender + ": " + message;
        this.serverUnicode = serverUnicode;
        this.index = index;
        this.receivers = receivers;
    }

    @Override
    public Object act() throws IOException {
        Model senderUser = MainServer.getUsers().get(sender);
        if (!message.equals(sender + ": #exit")) {
            // updating database and server
            synchronized (MainServer.getServers().get(serverUnicode).getTextChannels().get(index)) {
                MainServer.getServers().get(serverUnicode).getTextChannels().get(index).getMessages().add(message);
                boolean DBConnect = MainServer.updateDatabase(MainServer.getServers().get(serverUnicode));
                if (!DBConnect) return null;        // for debug
            }
            TextChannel updatedTextChannelFromMainServer = MainServer.getServers().get(serverUnicode).getTextChannels().get(index);

//            textChannel.getMessages().add(message);
//            MainServer.getServers().get(textChannel.getServerUnicode()).getTextChannels().set(textChannel.getId(), textChannel);

            // sending message from socket if the receiver is online and in the private chat
            for (ClientHandler c : clientHandlers) {
                Model userOfClientHandler = c.getUser();
                if (userOfClientHandler != null) {
                    if (receivers.contains(userOfClientHandler.getUsername())) {
                        userOfClientHandler = MainServer.getUsers().get(userOfClientHandler.getUsername()); // updating userOfClientHandler
                        if (updatedTextChannelFromMainServer.getMembers().get(userOfClientHandler.getUsername())) {
                            // synchronize!!!!!!!
                            //
                            //
                            //
                            synchronized (c.getMySocket()) {
                                c.getMySocket().write(message); // we can also write "this" object
                            }
                        }
                    }
                }
            }
            return null;  //receiver is not currently in the chat
        } else {
            return senderUser;
        }
    }
}