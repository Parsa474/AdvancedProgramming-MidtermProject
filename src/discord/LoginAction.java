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
        if (!MainServer.getUsers().containsKey(username)) {
            return null;
        } else if (!MainServer.getUsers().get(username).getPassword().equals(password)) {
            return null;
        } else {
            Model user = MainServer.getUsers().get(username);
            user.setStatus(Model.Status.Online);
            return user;
        }
    }
}
