package discord;

import java.util.ArrayList;

public class TextChannel {

    private final int id;
    private String name;
    private final ArrayList<String> members;
    private final ArrayList<String> messages;

    public TextChannel(int id, String name, ArrayList<String> members, ArrayList<String> messages) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }
}
