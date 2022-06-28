package discord;

import java.io.IOException;
import java.util.ArrayList;

import static discord.ClientHandler.clientHandlers;

public class TextChannelChatAction implements Action {
    // Fields:
    private String sender;
    private String message;
//    private TextChannel textChannel; // can be deleted
    private final int serverUnicode;
    private final int textChannelId;
    private ArrayList<String> receivers;  // members of the text-channel

    // Constructors:
//    public TextChannelChatAction(String sender, String message, TextChannel textChannel) {
//        this.sender = sender;
//        this.message = sender + ": " + message;
//        this.textChannel = textChannel;
//        serverUnicode = textChannel.getServerUnicode();
//        textChannelId = textChannel.getId();
//        receivers = new ArrayList<String>();
//        receivers.addAll(textChannel.getMembers().keySet());
//        receivers.remove(sender); // check kon ke hamuno remove kone
//    }

    public TextChannelChatAction(String sender, String message, int serverUnicode, int textChannelId, ArrayList<String> receivers) {
        this.sender = sender;
        this.message = message;
        this.serverUnicode = serverUnicode;
        this.textChannelId = textChannelId;
        this.receivers = receivers;
    }

    @Override
    public Object act() throws IOException {
        Model senderUser = MainServer.getUsers().get(sender);
        if (!message.equals(sender + ": #exit")) {
            // updating database and server
            synchronized (MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelId)) {
                MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelId).getMessages().add(message);
            }
            TextChannel updatedTextChannelFromMainServer = MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelId);
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
