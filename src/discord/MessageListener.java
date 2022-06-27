package discord;

import java.io.IOException;
import java.io.InterruptedIOException;

public class MessageListener implements Runnable{
    // Fields:
    private boolean exit;
    private final MySocket mySocket;
    private final View printer;

    // Constructors:
    public MessageListener(MySocket mySocket, View printer) {
        exit = false;
        this.mySocket = mySocket;
        this.printer = printer;
    }

    // Methods:
    @Override
    public void run() {
        Object inObject;
        while (mySocket.getConnectionSocket().isConnected()) {
            try {
                inObject = mySocket.read();
                if (inObject instanceof String) {
                    printer.println((String) inObject);
                } else if (inObject instanceof Boolean) {
                    if ((Boolean) inObject) { // seen by the friend
                        printer.println("(seen)");
                    }
                } else if (inObject instanceof Model) {

                    break;
                }
            } catch (InterruptedIOException e) {
                printer.println("bebinim chie");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
//                mySocket.closeEverything();
                break;
            }
        }
        printer.println("dige listen nemikonam");
    }
}
