import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Model implements Serializable {

    private String username;
    private String password;
    private String email;
    private String phoneNumber;

    private Socket socket;
//    private ObjectInputStream objectInputStream;
//    private ObjectOutputStream objectOutputStream;

    private final LinkedList<String> friendRequests;
    private final LinkedList<String> friends;
    private HashMap<String, ArrayList<Message>> privateChatMessages;
    private Status status;

    public Model(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        friendRequests = new LinkedList<>();
        friends = new LinkedList<>();
        privateChatMessages = new HashMap<String, ArrayList<Message>>();
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
//        try {
//            System.out.println("setSocket done11");
//            objectInputStream = new ObjectInputStream(this.socket.getInputStream());
//            System.out.println("setSocket done2");
//            objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
//            System.out.println("setSocket done3");
//        } catch (IOException e) {
//            System.out.println("I/O error occurred!");
//            e.printStackTrace();
//        }
    }

//    public ObjectInputStream getObjectInputStream() {
//        return objectInputStream;
//    }
//
//    public ObjectOutputStream getObjectOutputStream() {
//        return objectOutputStream;
//    }

    public LinkedList<String> getFriendRequests() {
        return friendRequests;
    }

    public LinkedList<String> getFriends() {
        return friends;
    }

    public HashMap<String, ArrayList<Message>> getPrivateChatMessages() {
        return privateChatMessages;
    }

    public void setPrivateChatMessages(HashMap<String, ArrayList<Message>> privateChatMessages) {
        this.privateChatMessages = privateChatMessages;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String toString() {
        return username + " " + password + " " + email + " " + phoneNumber;
    }
}
