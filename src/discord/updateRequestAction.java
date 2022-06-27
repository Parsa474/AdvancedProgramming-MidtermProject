package discord;

public class updateRequestAction extends Action {

    private final String username;

    public updateRequestAction(String username) {
        this.username = username;
    }

    @Override
    public Object act() {
        return MainServer.updateServerAndGetUser(username);
    }
}
