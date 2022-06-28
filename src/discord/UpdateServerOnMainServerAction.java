package discord;

import java.io.IOException;

public class UpdateServerOnMainServerAction implements Action {
    // Fields:
    private final Server server;

    // Constructors:
    public UpdateServerOnMainServerAction(Server server) {
        this.server = server;
    }

    // Methods:
    @Override
    public Object act() throws IOException {
        MainServer.getServers().replace(server.getUnicode(), server);
        MainServer.updateDatabase(server);
        return true;
    }
}
