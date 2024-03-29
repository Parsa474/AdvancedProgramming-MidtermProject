package discord;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MySocket {
    // Fields:
    private final Socket connectionSocket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    // Constructors:
    public MySocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        try {
            this.objectOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters:
    public boolean isConnected() {
        return connectionSocket.isConnected();
    }

    // Other Methods:
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
        objectOutputStream.reset();
        objectOutputStream.writeObject(object);
    }

    public <Type> Type read() throws IOException, ClassNotFoundException {
        return (Type) objectInputStream.readObject();
    }

    public <Type> Type sendSignalAndGetResponse(Object object) throws IOException, ClassNotFoundException {
        write(object);
        return read();
    }
}
