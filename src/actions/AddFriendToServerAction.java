package actions;

import mainServer.MainServer;
import discord.Model;

public class AddFriendToServerAction implements Action {

    // Fields:
    private final int unicode;
    private final String friendUsername;

    // Constructors:
    public AddFriendToServerAction(int unicode, String friendUsername) {
        this.unicode = unicode;
        this.friendUsername = friendUsername;
    }

    // Methods:
    @Override
    public Object act() {
        Model targetFriend = MainServer.getUsers().get(friendUsername);
        targetFriend.getServers().add(unicode);
        return MainServer.updateDatabase(targetFriend);
    }
}
