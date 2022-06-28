package discord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TextChannel implements Serializable {
    // Fields:
    private final int serverUnicode;
    private final int id;
    private String name;
    private final HashMap<String, Boolean> members;
    private final ArrayList<String> messages;

    // Constructors:
    public TextChannel(int serverUnicode, int id, String name, HashMap<String, Boolean> members, ArrayList<String> messages) {
        this.serverUnicode = serverUnicode;
        this.id = id;
        this.name = name;
        this.members = members;
        this.messages = messages;
    }

    public TextChannel(int serverUnicode, int id, String name, ArrayList<String> members, ArrayList<String> messages) {
        this.serverUnicode = serverUnicode;
        this.id = id;
        this.name = name;
        this.members = new HashMap<String, Boolean>();
        for (String member: members) {
            this.members.put(member, false);
        }
        this.messages = messages;
    }

    // Methods:
    public int getServerUnicode() {
        return serverUnicode;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void enter(ClientController clientController) {
        clientController.textChannelChat(this);
    }
}
