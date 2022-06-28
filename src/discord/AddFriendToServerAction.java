package discord;

import java.io.IOException;

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
    public Object act() throws IOException {
        Model targetFriend = MainServer.getUsers().get(friendUsername);
        targetFriend.getServers().add(unicode);
        MainServer.updateDatabase(targetFriend);
        return true;
    }
}
