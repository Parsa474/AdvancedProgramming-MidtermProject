package discord;

import java.io.IOException;

public class AddFriendToServerAction extends Action {

    private final int unicode;
    private final String friendUsername;

    public AddFriendToServerAction(int unicode, String friendUsername) {
        this.unicode = unicode;
        this.friendUsername = friendUsername;
    }

    @Override
    public Object act() throws IOException {
        Model targetFriend = MainServer.getUsers().get(friendUsername);
        targetFriend.getServers().add(unicode);
        MainServer.updateDatabase(targetFriend);
        return true;
    }
}
