package discord;

public class SignupAction extends Action {

    private final Model user;

    public SignupAction(Model user) {
        this.user = user;
    }

    @Override
    public Object act() {
        MainServer.signUpUser(user);
        return true;
    }
}
