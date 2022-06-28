package discord;

import java.io.IOException;

public class UpdateServerAction extends Action {

    private final Server server;

    public UpdateServerAction(Server server) {
        this.server = server;
    }

    @Override
    public Object act() throws IOException {
        MainServer.getServers().replace(server.getUnicode(), server);
        MainServer.updateDatabase(server);
        return true;
    }
}
