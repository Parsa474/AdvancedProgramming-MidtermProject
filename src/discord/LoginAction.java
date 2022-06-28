package discord;

public class LoginAction implements Action {
    // Fields:
    private final String username;
    private final String password;

    // Constructors:
    public LoginAction(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Methods:
    @Override
    public Object act() {
        if (!MainServer.getUsers().containsKey(username)) {
            return null;
        } else if (!MainServer.getUsers().get(username).getPassword().equals(password)) {
            return null;
        } else {
            Model user = MainServer.getUsers().get(username);
            user.setStatus(Status.Online);
            return user;
        }
    }
}
