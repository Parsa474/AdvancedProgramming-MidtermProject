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
        while (mySocket.getConnectionSocket().isConnected() && !exit) {
            try {
                inObject = mySocket.read();
                if (inObject instanceof String) {
                    String message = (String) inObject;
                    if (message.equals("#exit")) {
                        break;
                    }
                    // else
                    printer.println(message);
                } else if (inObject instanceof Boolean) {
                    if ((Boolean) inObject) { // seen by the friend
                        printer.println("(seen)");
                    }
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

    public void shutdown() {
        exit = true;
    }
}
