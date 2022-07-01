package signals;

import discord.TextChannelMessage;
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
            if (message.startsWith("/") || message.startsWith("#")) {
                if (message.equals("#pin")) {
                    return "PINNED MESSAGE: " + MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getPinnedMessage();
                }
                String[] command = message.split(" ");
                int indexOfMessage = checkIndex(MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex), command[1]);
                if (indexOfMessage == -1) {
                    return "WARNING: Invalid format";
                } else if (indexOfMessage == -2) {
                    return "WARNING: invalid number of message. out of boundary";
                } else {
                    switch (command[0]) {
                        case "/pin" -> {
                            pinMessage(MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex), indexOfMessage);
                            message = "NOTIFICATION: " + sender + " pinned \"" + MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getMessages().get(indexOfMessage) + "\"";
                        }
                        case "/like" -> {
                            likeMessage(MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex), indexOfMessage);
                            message = "NOTIFICATION: " + sender + " liked \"" + MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getMessages().get(indexOfMessage) + "\"";
                        }
                        case "/dislike" -> {
                            dislikeMessage(MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex), indexOfMessage);
                            message = "NOTIFICATION: " + sender + " disliked \"" + MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getMessages().get(indexOfMessage) + "\"";
                        }
                        case "/laugh" -> {
                            laughMessage(MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex), indexOfMessage);
                            message = "NOTIFICATION: " + sender + " laughed \"" + MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getMessages().get(indexOfMessage) + "\"";
                        }


                        case "#likes" -> {
                            return MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getTextChannelMessages().get(indexOfMessage).showLikes();
                        }
                        case "#dislikes" -> {
                            return MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getTextChannelMessages().get(indexOfMessage).showDislikes();
                        }
                        case "#laughs" -> {
                            return MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getTextChannelMessages().get(indexOfMessage).showLaugh();
                        }
                        case "#reactions" -> {
                            return MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getTextChannelMessages().get(indexOfMessage).showAllReactions();
                        }
                    }
                }
            }
            TextChannelMessage textChannelMessage = new TextChannelMessage(message);
            // updating database and server
            synchronized (MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex)) {
                if (!message.startsWith("NOTIFICATION: ")) {
                    textChannelMessage.setMessage((MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getMessages().size() + 1) + "- " + sender + ": " + textChannelMessage.getMessage());
                    MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex).getTextChannelMessages().add(textChannelMessage);
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
                                c.getMySocket().write(textChannelMessage); // we can also write "this" object
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

    private int checkIndex(TextChannel textChannel, String message) {
        try {
            int index = Integer.parseInt(message) - 1;
            if (index >= 0 && index < textChannel.getMessages().size()) {
                return index;
            } else {
                return -2;  // out of boundary
            }
        } catch (NumberFormatException e) {
            return -1;  // wrong format
        }
    }

    public String showPinnedMessage(TextChannel textChannel) {
        return textChannel.getPinnedMessage();
    }

    private void pinMessage(TextChannel textChannel, int index) {
        textChannel.setPinnedMessage(textChannel.getMessages().get(index));
    }

    private void likeMessage(TextChannel textChannel, int index) {
        textChannel.getTextChannelMessages().get(index).like(sender);
    }

    private void dislikeMessage(TextChannel textChannel, int index) {
        textChannel.getTextChannelMessages().get(index).dislike(sender);
    }

    private void laughMessage(TextChannel textChannel, int index) {
        textChannel.getTextChannelMessages().get(index).laugh(sender);
    }
}