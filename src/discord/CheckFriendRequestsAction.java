package discord;

import java.util.ArrayList;

public class CheckFriendRequestsAction extends Action {

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
            return false;
        }
        Model user = MainServer.getUsers().get(username);
        String requesterUsername = user.getFriendRequests().get(index);
        Model requester = MainServer.getUsers().get(requesterUsername);
        if (accept) {
            user.getFriends().add(requesterUsername);
            requester.getFriends().add(username);
            user.getIsInChat().put(requesterUsername, false);
            user.getPrivateChats().put(requesterUsername, new ArrayList<>());
            requester.getIsInChat().put(username, false);
            requester.getPrivateChats().put(username, new ArrayList<>());
            MainServer.updateDatabase(requester);
        }
        user.getFriendRequests().remove(index);
        MainServer.updateDatabase(user);
        return true;
    }
}
