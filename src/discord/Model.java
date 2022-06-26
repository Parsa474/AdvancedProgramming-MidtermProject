package discord;

import java.io.Serializable;
import java.util.LinkedList;

public class Model implements Serializable {

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private LinkedList<String> friendRequests;
    private LinkedList<String> friends;
    private Status status;

    public Model(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.friendRequests = new LinkedList<>();
        this.friends = new LinkedList<>();
        status = Status.Offline;
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

    public LinkedList<String> getFriendRequests() {
        return friendRequests;
    }

    public LinkedList<String> getFriends() {
        return friends;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setFriendRequests(LinkedList<String> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public void setFriends(LinkedList<String> friends) {
        this.friends = friends;
    }

    public String toString() {
        return username + " " + password + " " + email + " " + phoneNumber + '\n' + friendRequests;
    }
}
