package actions;

import mainServer.MainServer;
import discord.Model;

public class SendFriendRequestAction implements Action {
    // Fields:
    private final String requester;
    private final String username;

    // Constructors:
    public SendFriendRequestAction(String requester, String username) {
        this.requester = requester;
        this.username = username;
    }

    // Methods:
    @Override
    public Object act() {
        if (!MainServer.getUsers().containsKey(username)) {
            return null;
        } else {
            Model user = MainServer.getUsers().get(username);
            if (user.getFriendRequests().contains(requester)) {
                return false;
            }
            user.getFriendRequests().add(requester);
            if (!MainServer.updateDatabase(user)) {
                return null;
            }
            return true;
        }
    }
}