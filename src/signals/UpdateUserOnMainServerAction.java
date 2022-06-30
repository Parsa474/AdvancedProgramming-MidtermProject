package signals;

import mainServer.MainServer;
import discord.Model;

public class UpdateUserOnMainServerAction implements Action {
    private final Model me;

    public UpdateUserOnMainServerAction(Model me) {
        this.me = me;
    }

    @Override
    public Object act() {
        MainServer.getUsers().replace(me.getUsername(), me);
        return MainServer.updateDatabase(me);
    }
}