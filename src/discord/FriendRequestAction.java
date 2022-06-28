package discord;

public class FriendRequestAction implements Action {
    // Fields:
    private final String requester;
    private final String username;

    // Constructors:
    public FriendRequestAction(String requester, String username) {
        this.requester = requester;
        this.username = username;
    }

    // Methods:
    @Override
    public Object act() {
        if (!MainServer.getUsers().containsKey(username)) {
            return false;
        } else {
            Model user = MainServer.getUsers().get(username);
            if (user.getFriendRequests().contains(requester)) {
                return false;
            }
            user.getFriendRequests().add(requester);
            MainServer.updateDatabase(user);
            return true;
        }
    }
}
