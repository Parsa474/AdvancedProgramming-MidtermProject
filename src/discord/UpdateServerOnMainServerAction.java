package discord;

import java.io.IOException;

public class UpdateServerOnMainServerAction implements Action {

    private final Server server;

    public UpdateServerOnMainServerAction(Server server) {
        this.server = server;
    }

    @Override
    public Object act() throws IOException {
        MainServer.getServers().replace(server.getUnicode(), server);
        MainServer.updateDatabase(server);
        return true;
    }
}
