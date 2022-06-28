package discord;

import java.io.IOException;

public class AddNewServerToDatabaseAction implements Action {

    // Fields:
    private final Server newServer;

    // Constructors:
    public AddNewServerToDatabaseAction(Server newServer) {
        this.newServer = newServer;
    }

    // Methods:
    @Override
    public Object act() throws IOException {
        MainServer.getServers().put(newServer.getUnicode(), newServer);
        MainServer.updateDatabase(newServer);
        return true;
    }
}
