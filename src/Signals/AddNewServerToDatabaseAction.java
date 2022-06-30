package Signals;

import mainServer.MainServer;
import discord.Server;

public class AddNewServerToDatabaseAction implements Action {

    // Fields:
    private final Server newServer;

    // Constructors:
    public AddNewServerToDatabaseAction(Server newServer) {
        this.newServer = newServer;
    }

    // Methods:
    @Override
    public Object act() {
        MainServer.getServers().put(newServer.getUnicode(), newServer);
        return MainServer.updateDatabase(newServer);
    }
}
