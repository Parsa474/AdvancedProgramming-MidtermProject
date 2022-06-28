package discord;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Server implements Serializable {

    private final int unicode;
    private String serverName;
    private final ArrayList<String> members;
    private final ArrayList<TextChannel> textChannels;

    public Server(int unicode, String serverName, String creator) {
        this.unicode = unicode;
        this.serverName = serverName;
        members = new ArrayList<>();
        members.add(creator);
        textChannels = new ArrayList<>();
        textChannels.add(new TextChannel(unicode, 0, "general",members, new ArrayList<>()));
    }

    public int getUnicode() {
        return unicode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public ArrayList<TextChannel> getTextChannels() {
        return textChannels;
    }

    public Object enter(ClientController clientController) throws IOException {
        clientController.getPrinter().printServerMenu();
        int command = MyScanner.getInt(1, 5);
        switch (command) {
            case 1 -> changeInfo(clientController);
            case 2 -> changeMembers(clientController);
            case 3 -> changeTextChannels(clientController);
            case 4 -> {
                return enterATextChannel(clientController);
            }
        }
        return null;
    }

    private void changeInfo(ClientController clientController) throws IOException {
        clientController.getPrinter().printServerChangeInfoMenu();
        int command = MyScanner.getInt(1, 4);
        switch (command) {
            case 1 -> {
                clientController.getPrinter().printGetMessage("new server name");
                serverName = MyScanner.getLine();
            }
            case 2 -> {
                clientController.getPrinter().printTextChannelList(textChannels);
                int index = MyScanner.getInt(1, textChannels.size());
                clientController.getPrinter().printGetMessage("new text channel name");
                String newTextChannelName = MyScanner.getLine();
                textChannels.get(index).setName(newTextChannelName);

            }
            case 3 -> {
                //delete this server!
            }
        }
        clientController.getMySocket().write(new UpdateServerOnMainServerAction(this));
    }

    private void changeMembers(ClientController clientController) {

    }

    private void changeTextChannels(ClientController clientController) {

    }

    private TextChannel enterATextChannel(ClientController clientController) {
        clientController.getPrinter().printTextChannelList(textChannels);
        int index = MyScanner.getInt(1, textChannels.size()) - 1;
        return textChannels.get(index);
    }
}
