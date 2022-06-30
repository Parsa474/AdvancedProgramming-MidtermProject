package Signals;

import mainServer.MainServer;

public class GetUserFromMainServerAction implements Action {
    // Fields:
    private final String username;

    // Constructors:
    public GetUserFromMainServerAction(String username) {
        this.username = username;
    }

    // Methods:
    @Override
    public Object act() {
        return MainServer.getUsers().getOrDefault(username, null);
    }
}
