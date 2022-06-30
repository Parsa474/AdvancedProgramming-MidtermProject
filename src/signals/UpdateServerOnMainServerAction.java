package signals;

import mainServer.MainServer;
import discord.Server;

public class UpdateServerOnMainServerAction implements Action {
    private final Server server;

    public UpdateServerOnMainServerAction(Server server) {
        this.server = server;
    }

    @Override
    public Object act() {
        MainServer.getServers().replace(server.getUnicode(), server);
        return MainServer.updateDatabase(server);
    }
}
