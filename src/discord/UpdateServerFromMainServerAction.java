package discord;

import java.io.IOException;

public class UpdateServerFromMainServerAction implements Action {
    // Fields:
    private final int unicode;

    // Constructors:
    public UpdateServerFromMainServerAction(int unicode) {
        this.unicode = unicode;
    }

    // Methods:
    @Override
    public Object act() throws IOException {
        return MainServer.getServers().get(unicode);
    }
}
