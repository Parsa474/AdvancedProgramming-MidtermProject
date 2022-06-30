package Signals;

import mainServer.MainServer;
import discord.Server;

public class UpdateServerOnMainServerAction implements Action {
    // Fields:
    private final Server server;

    // Constructors:
    public UpdateServerOnMainServerAction(Server server) {
        this.server = server;
    }

    // Methods:
    @Override
    public Object act() {
        MainServer.getServers().replace(server.getUnicode(), server);
        return MainServer.updateDatabase(server);
    }
}
