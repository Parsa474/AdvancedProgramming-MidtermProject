package discord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TextChannel implements Serializable {

    private final int serverUnicode;
    private final int id;
    private String name;
    private final HashMap<String, Boolean> members;
    private final ArrayList<String> messages;

    public TextChannel(int serverUnicode, int id, String name, ArrayList<String> members, ArrayList<String> messages) {
        this.serverUnicode = serverUnicode;
        this.id = id;
        this.name = name;
        this.members = new HashMap<>();
        for (String member : members) {
            this.members.put(member, false);
        }
        this.messages = messages;
    }

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

    public void enter() {

    }
}
