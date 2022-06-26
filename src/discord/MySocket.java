package discord;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class MySocket {

    private Socket connectionSocket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public MySocket(Socket connectionSocket) {
        try {
            this.connectionSocket = connectionSocket;
            this.objectOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    public void closeEverything() {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (connectionSocket != null) {
                connectionSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(Object object) throws IOException {
        objectOutputStream.writeObject(object);
    }

    public Object read() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }

    public Action readAction() throws IOException, ClassNotFoundException {
        return (Action) objectInputStream.readObject();
    }

    public String readString() throws IOException, ClassNotFoundException {
        return (String) objectInputStream.readObject();
    }

    public Model readModel() throws IOException, ClassNotFoundException {
        return (Model) objectInputStream.readObject();
    }

    public boolean readBoolean() throws IOException, ClassNotFoundException {
        return (boolean) objectInputStream.readObject();
    }

    public void flush() throws IOException {
        objectOutputStream.flush();
    }

    public LinkedList<String> readStringLinkedList() throws IOException, ClassNotFoundException {
        return (LinkedList<String>) objectInputStream.readObject();
    }
}
