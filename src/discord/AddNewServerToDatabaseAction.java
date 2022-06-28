package discord;

import java.io.IOException;

public class AddNewServerToDatabaseAction implements Action {

    private final Server newServer;

    public AddNewServerToDatabaseAction(Server newServer) {
        this.newServer = newServer;
    }

    @Override
    public Object act() throws IOException {
        MainServer.getServers().put(newServer.getUnicode(), newServer);
        MainServer.updateDatabase(newServer);
        return MainServer.getServers().get(newServer.getUnicode()).getMembers();
    }
}
