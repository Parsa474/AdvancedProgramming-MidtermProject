package discord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Model implements Serializable {

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
    }

    public enum Status {
        Online, Idle, DoNotDisturb, Invisible, Offline
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public String toString() {
        return username + " " + password + " " + email + " " + phoneNumber;
    }
}
