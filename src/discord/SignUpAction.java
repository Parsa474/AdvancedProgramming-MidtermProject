package discord;

public class SignUpAction extends Action {

    private final Model user;

    public SignUpAction(Model user) {
        this.user = user;
    }

    @Override
    public Object act() {
        MainServer.signUpUser(user);
        return user;
    }
}
