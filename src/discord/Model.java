package discord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Model implements Serializable {
    // Fields:
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private Status status;
    private final LinkedList<String> friendRequests;
    private final LinkedList<String> friends;
    private final HashMap<String, Boolean> isInChat;
    private final HashMap<String, ArrayList<String>> privateChats;
    private final ArrayList<Integer> servers;
//    private ArrayList<TextChannel> textChannels;
//    private HashMap<TextChannel, Boolean> isInTextChannel;

    // Constructors:
    public Model(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        status = Status.Offline;
        friendRequests = new LinkedList<>();
        friends = new LinkedList<>();
        isInChat = new HashMap<>();
        privateChats = new HashMap<>();
        servers = new ArrayList<>();
//        textChannels = new ArrayList<TextChannel>();
//        isInTextChannel = new HashMap<TextChannel, Boolean>();
    }

    public enum Status {
        Online, Idle, DoNotDisturb, Invisible, Offline
    }

    // Methods:
    // Getter Methods:
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Status getStatus() {
        return status;
    }

    public LinkedList<String> getFriendRequests() {
        return friendRequests;
    }

    public LinkedList<String> getFriends() {
        return friends;
    }

    public HashMap<String, Boolean> getIsInChat() {
        return isInChat;
    }

    public HashMap<String, ArrayList<String>> getPrivateChats() {
        return privateChats;
    }

    public ArrayList<Integer> getServers() {
        return servers;
    }

//    public ArrayList<TextChannel> getTextChannels() {
//        return textChannels;
//    }
//
//    public HashMap<TextChannel, Boolean> getIsInTextChannel() {
//        return isInTextChannel;
//    }

    // Setter Methods:
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

//    public void setTextChannels(ArrayList<TextChannel> textChannels) {
//        this.textChannels = textChannels;
//    }
//
//    public void setIsInTextChannel(HashMap<TextChannel, Boolean> isInTextChannel) {
//        this.isInTextChannel = isInTextChannel;
//    }

    // Other Methods:
//    public ArrayList<TextChannel> getTextChannels() {
//        for (Integer i : servers) {
//
//        }
//    }

    public String toString() {
        return username + " " + password + " " + email + " " + phoneNumber;
    }
}
