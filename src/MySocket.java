import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MySocket {
    // Fields:
    private Socket connectionSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    // Constructors:
    public MySocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        try {
            inputStream = new ObjectInputStream(connectionSocket.getInputStream());
            outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methods:
    public Message read() throws IOException, ClassNotFoundException {
        return (Message) inputStream.readObject();
    }

    public void write(Message message) throws IOException {
        outputStream.writeObject(message);
    }

    public boolean isConnected() {
        return connectionSocket.isConnected();
    }
}
