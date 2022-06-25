package discord;

public class LoginAction extends Action {

    private final String username;
    private final String password;

    public LoginAction(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Object act() {
        if (!MainServer.users.containsKey(username)) {
            return null;
        } else if (!MainServer.users.get(username).getPassword().equals(password)) {
            return null;
        } else {
            return MainServer.users.get(username);
        }
    }
}
