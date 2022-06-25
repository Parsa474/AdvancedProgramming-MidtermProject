package discord;

public class FriendRequestAction extends Action {

    private final String requester;
    private final String username;

    public FriendRequestAction(String requester, String username) {
        this.requester = requester;
        this.username = username;
    }

    @Override
    public Object act() {
        if (!MainServer.users.containsKey(username)) {
            return false;
        } else {
            Model user = MainServer.users.get(username);
            if (user.getFriendRequests().contains(requester)) {
                return false;
            }
            user.getFriendRequests().add(requester);
            MainServer.updateDatabase(user);
            return true;
        }
    }
}
