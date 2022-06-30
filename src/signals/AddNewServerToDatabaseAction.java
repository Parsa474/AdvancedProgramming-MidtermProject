package signals;

import mainServer.MainServer;
import discord.Server;

public class AddNewServerToDatabaseAction implements Action {
    private final Server newServer;

    public AddNewServerToDatabaseAction(Server newServer) {
        this.newServer = newServer;
    }

    @Override
    public Object act() {
        MainServer.getServers().put(newServer.getUnicode(), newServer);
        return MainServer.updateDatabase(newServer);
    }
}
