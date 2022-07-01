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
    private String message;
    private final int serverUnicode;
    private final int textChannelIndex;
    private final ArrayList<String> receivers;  // all the members of the textChannel except the sender

    public TextChannelChatAction(String sender, String message, int serverUnicode, int textChannelIndex, ArrayList<String> receivers) {
        this.sender = sender;
        message = message.trim();
        this.message = message;
        this.serverUnicode = serverUnicode;
        this.textChannelIndex = textChannelIndex;
        this.receivers = receivers;
    }

    @Override
    public Object act() throws IOException {
        Model senderUser = MainServer.getUsers().get(sender);
        if (!message.equals("#exit")) {
            if (message.startsWith("/pin ")) {
                message = message.substring(5);
                int indexOfMessage = pinMessage(MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex), message);
                if (indexOfMessage == -1) {
                    return "WARNING: Invalid format for pinning a message";
                } else if (indexOfMessage == -2) {
                    return "WARNING: invalid number of message to pin. out of boundary";
                } else {
                    message = "NOTIFICATION: " + sender + " pinned \"" + MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getMessages().get(indexOfMessage) + "\"";
                }
            } else if (message.equals("#pin")) {  // shows the pinned message of the textChannel
                message = "PINNED MESSAGE: " + MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getPinnedMessage();
                return message;
            } else {
                message = sender + ": " + message;
            }
            // updating database and server
            synchronized (MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex)) {
                if (!message.startsWith("NOTIFICATION: ")) {
                    message = (MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getMessages().size() + 1) + "_ " + message;
                    MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getMessages().add(message);
                }
                boolean DBConnect = MainServer.updateDatabase(MainServer.getServers().get(serverUnicode));
                if (!DBConnect) return null;        // for debug
            }
            TextChannel updatedTextChannelFromMainServer = MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex);

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

    public String showPinnedMessage(TextChannel textChannel) {
        return textChannel.getPinnedMessage();
    }

    private int pinMessage(TextChannel textChannel, String message) {
        try {
            int index = Integer.parseInt(message) - 1;
            if (index >= 0 && index < textChannel.getMessages().size()) {
                textChannel.setPinnedMessage(textChannel.getMessages().get(index));
                return index;
            } else {
                return -2;  // out of boundary
            }
        } catch (NumberFormatException e) {
            return -1;  // wrong format
        }
    }
}