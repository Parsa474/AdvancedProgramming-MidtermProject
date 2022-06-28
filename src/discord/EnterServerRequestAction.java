package discord;

import java.io.IOException;

public class EnterServerRequestAction extends Action {

    private final int unicode;

    public EnterServerRequestAction(int unicode) {
        this.unicode = unicode;
    }

    @Override
    public Object act() throws IOException {
        return MainServer.getServers().get(unicode);
    }
}
