package discord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TextChannel implements Serializable {
    // Fields:
    private String name;
    private String pinnedMessage;
    private final HashMap<String, Boolean> members;
    // maps all the members' username to whether they're in this text channel right now (true) or not (false)
    private final ArrayList<String> messages;
    // holds all the messages exchanged in this text channel

    // Constructors:
    public TextChannel(String name, ArrayList<String> members, ArrayList<String> messages) {
        this.name = name;
        pinnedMessage = null;
        this.members = new HashMap<>();
        for (String member : members) {
            this.members.put(member, false);
        }
        this.messages = messages;
    }

    // Getters:
    public String getName() {
        return name;
    }

    public String getPinnedMessage() {
        return pinnedMessage;
    }

    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    // Setters:
    public void setName(String name) {
        this.name = name;
    }

    public void setPinnedMessage(String pinnedMessage) {
        this.pinnedMessage = pinnedMessage;
    }
}
