import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class privateChatClientHandler implements Runnable {

    // Fields:
    private MySocket srcUser;
    private Model dstUser;
    private MySocket dstUserMySocket;
    private ArrayList<MySocket> allConnectionSockets;
    private HashMap<String, Model> allUsers;

    // Constructors:
    public privateChatClientHandler(MySocket srcUser, ArrayList<MySocket> allConnectionSockets, HashMap<String, Model> allUsers) {
        this.srcUser = srcUser;
//        if (dstUser.getSocket().isConnected()) { // null?????????????????????????????????????????????????????????????????????????????
//            this.dstUserMySocket = new MySocket(dstUser.getSocket());
//        }
        this.allConnectionSockets = allConnectionSockets;
        this.allUsers = allUsers;
    }

    // Methods:
    @Override
    public void run() {
        while (true) {
            try {
                Message receivedMessage = srcUser.read();
                if (receivedMessage.equals("\n")) {
                    break;
                }
                String[] receivers = receivedMessage.getReceiver();
                for (int i = 0; i < receivers.length - 1; ++i) {
                    if (allUsers.get(receivers[i]).getSocket().isConnected()) {
                        dstUserMySocket.write(srcUser.read());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
