package discord;

public class UpdateRequestAction extends Action {

    private final String username;

    public UpdateRequestAction(String username) {
        this.username = username;
    }

    @Override
    public Object act() {
        return MainServer.GetUserFromServer(username);
    }
}
