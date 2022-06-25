package discord;

public class LaunchAction extends Action {

    private Model user;
    private String username;
    private String password;
    private final boolean signup;

    public LaunchAction(Model user) {
        this.user = user;
        this.signup = true;
    }

    public LaunchAction(String username, String password) {
        this.username = username;
        this.password = password;
        this.signup = false;
    }

    @Override
    public Object act() {
        if (signup) {
            MainServer.signUpUser(user);
        } else {
            if (!MainServer.users.containsKey(username)) {
                return null;
            } else if (!MainServer.users.get(username).getPassword().equals(password)) {
                return null;
            } else {
                user = MainServer.users.get(username);
            }
        }
        return user;
    }
}
