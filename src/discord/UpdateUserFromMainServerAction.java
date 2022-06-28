package discord;

public class UpdateUserFromMainServerAction implements Action {
    // Fields:
    private final String username;

    // Constructors:
    public UpdateUserFromMainServerAction(String username) {
        this.username = username;
    }

    // Methods:
    @Override
    public Object act() {
        return MainServer.getUserFromMainServer(username);
    }
}
