package discord;

public class UpdateUserFromMainServerAction extends Action {

    private final String username;

    public UpdateUserFromMainServerAction(String username) {
        this.username = username;
    }

    @Override
    public Object act() {
        return MainServer.GetUserFromServer(username);
    }
}