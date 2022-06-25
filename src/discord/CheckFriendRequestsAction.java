package discord;

import java.util.ArrayList;

public class CheckFriendRequestsAction extends Action {

    private final Model user;
    private final ArrayList<Integer> acceptedIndexes;
    private final ArrayList<Integer> rejectedIndexes;

    public CheckFriendRequestsAction(Model user, ArrayList<Integer> acceptedIndexes, ArrayList<Integer> rejectedIndexes) {
        this.user = user;
        this.acceptedIndexes = acceptedIndexes;
        this.rejectedIndexes = rejectedIndexes;
    }

    @Override
    public Object act() {
        for (int i : acceptedIndexes) {
            user.getFriends().add(user.getFriendRequests().get(i - 1));
            user.getFriendRequests().remove(i - 1);
        }
        for (int i : rejectedIndexes) {
            user.getFriendRequests().remove(i - 1);
        }
        MainServer.updateDatabase(user);
        return new Object();
    }
}
