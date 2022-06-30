package discord;

import signals.DBConnectFailSignal;

import java.io.IOException;

public class TextChannelListener implements Runnable {

    private final ClientController clientController;

    public TextChannelListener(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void run() {

        Model user = clientController.getUser();
        MySocket mySocket = clientController.getMySocket();
        View printer = clientController.getPrinter();

        Object inObject;
        while (mySocket.isConnected()) {
            try {
                inObject = mySocket.read();
                if (inObject instanceof DBConnectFailSignal) {
                    printer.printErrorMessage("db");
                    synchronized (user.getUsername()) {
                        user.getUsername().notify();
                    }
                    break;
                } else if (inObject instanceof String) {    // The String signals are the messages from the friend
                    printer.println((String) inObject);
                } else if (inObject instanceof Boolean) {
                    if ((Boolean) inObject) {    // true if seen by the friend immediately
                        printer.println("(seen)");
                    }
                } else if (inObject instanceof Model) {
                    synchronized (user.getUsername()) {
                        user.getUsername().notify();
                        //user = (Model) inObject;
                    }
                    break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                mySocket.closeEverything();
                break;
            }
        }
        printer.printSuccessMessage("exit");
    }
}
