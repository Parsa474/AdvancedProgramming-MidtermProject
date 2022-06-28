package discord;

import java.io.IOException;

public class UpdateServerFromMainServerAction extends Action {

    private final int unicode;

    public UpdateServerFromMainServerAction(int unicode) {
        this.unicode = unicode;
    }

    @Override
    public Object act() throws IOException {
        return MainServer.getServers().get(unicode);
    }
}
