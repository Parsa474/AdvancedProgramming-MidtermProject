package actions;

import mainServer.MainServer;

public class CreateNewServerAction implements Action {

    // Methods:
    @Override
    public Object act() {
        int unicode = 0;
        do {
            unicode++;
        } while (MainServer.getServers().containsKey(unicode));
        return unicode;
    }
}
