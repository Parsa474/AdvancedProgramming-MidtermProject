package discord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Model implements Serializable {

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private LinkedList<String> friendRequests;
    private LinkedList<String> friends;
    private HashMap<String, Boolean> isInChat;
    private HashMap<String, ArrayList<String>> privateChats;
    private Status status;

    public Model(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        friendRequests = new LinkedList<>();
        friends = new LinkedList<>();
        isInChat = new HashMap<String, Boolean>();
        privateChats = new HashMap<String, ArrayList<String>>();
        status = Status.Offline;
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

    public Status getStatus() {
        return status;
    }

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

    public void setFriendRequests(LinkedList<String> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public void setFriends(LinkedList<String> friends) {
        this.friends = friends;
    }

    public void setIsInChat(HashMap<String, Boolean> isInChat) {
        this.isInChat = isInChat;
    }

    public void setPrivateChats(HashMap<String, ArrayList<String>> privateChats) {
        this.privateChats = privateChats;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String toString() {
        return username + " " + password + " " + email + " " + phoneNumber;
    }
}
