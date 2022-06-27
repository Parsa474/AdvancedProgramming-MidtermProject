package discord;

import java.io.IOException;

public class UpdateMyUserAction extends Action{
    // Fields:
    private final Model me;

    // Constructors:
    public UpdateMyUserAction(Model me) {
        this.me = me;
    }

    // Methods:
    @Override
    public Object act() throws IOException {
        MainServer.getUsers().replace(me.getUsername(), me);
        MainServer.updateDatabase(me);
        return true;
    }
}
