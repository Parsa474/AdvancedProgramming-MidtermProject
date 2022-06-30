package Signals;

import mainServer.MainServer;

public class GetServerFromMainServerAction implements Action {
    // Fields:
    private final int unicode;

    // Constructors:
    public GetServerFromMainServerAction(int unicode) {
        this.unicode = unicode;
    }

    // Methods:
    @Override
    public Object act() {
        return MainServer.getServers().getOrDefault(unicode, null);
    }
}
