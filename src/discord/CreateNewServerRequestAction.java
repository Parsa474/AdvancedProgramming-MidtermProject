package discord;

import java.io.IOException;

public class CreateNewServerRequestAction implements Action {

    @Override
    public Object act() throws IOException {
        for (int unicode = 0; unicode < 100; unicode++) {
            if (!MainServer.getServers().containsKey(unicode)) {
                return unicode;
            }
        }
        return -1;
    }
}
