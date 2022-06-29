package actions;

import mainServer.MainServer;
import discord.Model;

public class UpdateUserOnMainServerAction implements Action {
    // Fields:
    private final Model me;

    // Constructors:
    public UpdateUserOnMainServerAction(Model me) {
        this.me = me;
    }

    // Methods:
    @Override
    public Object act() {
        MainServer.getUsers().replace(me.getUsername(), me);
        return MainServer.updateDatabase(me);
    }
}