package discord;

import java.io.IOException;

public class CreateNewServerRequestAction implements Action {

    // Methods:
    @Override
    public Object act() throws IOException {
        int unicode = -1;
        do {
            unicode++;
        } while (MainServer.getServers().containsKey(unicode));
        return unicode;
    }
}
