package signals;

import mainServer.MainServer;
import discord.Model;

import java.util.ArrayList;

public class CheckFriendRequestsAction implements Action {
    private final String username;
    private final int index;
    private final boolean accept;

    public CheckFriendRequestsAction(String username, int index, boolean accept) {
        this.username = username;
        this.index = index;
        this.accept = accept;
    }

    @Override
    public Object act() {
        if (!MainServer.getUsers().containsKey(username)) {
            return null;
        }
        Model user = MainServer.getUsers().get(username);
        String requesterUsername = user.getFriendRequests().get(index);
        Model requester = MainServer.getUsers().get(requesterUsername);
        boolean DBConnect = true;
        if (accept) {

            user.getFriends().add(requesterUsername);
            requester.getFriends().add(username);


            user.getIsInChat().put(requesterUsername, false);
            user.getPrivateChats().put(requesterUsername, new ArrayList<>());

            requester.getIsInChat().put(username, false);
            requester.getPrivateChats().put(username, new ArrayList<>());

            DBConnect = MainServer.updateDatabase(requester);
        }
        user.getFriendRequests().remove(index);
        DBConnect = DBConnect && MainServer.updateDatabase(user);
        return DBConnect;
    }
}
