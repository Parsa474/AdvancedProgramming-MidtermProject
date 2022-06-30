package signals;

import mainServer.MainServer;

public class GetUserFromMainServerAction implements Action {
    private final String username;

    public GetUserFromMainServerAction(String username) {
        this.username = username;
    }

    @Override
    public Object act() {
        return MainServer.getUsers().getOrDefault(username, null);
    }
}
