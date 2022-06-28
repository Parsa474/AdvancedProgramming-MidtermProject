package discord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Server implements Serializable {

    private final int unicode;
    private String serverName;
    private final ArrayList<String> members;
    private final ArrayList<TextChannel> textChannels;

    public Server(int unicode, String serverName, String creator) {
        this.unicode = unicode;
        this.serverName = serverName;
        members = new ArrayList<>();
        members.add(creator);
        textChannels = new ArrayList<>();
    }

    public int getUnicode() {
        return unicode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public ArrayList<TextChannel> getTextChannels() {
        return textChannels;
    }

    public void enter() {

    }
}
