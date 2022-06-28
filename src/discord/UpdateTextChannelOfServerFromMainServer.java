package discord;

import java.io.IOException;

public class UpdateTextChannelOfServerFromMainServer extends Action{
    // Fields:
    private final int serverUnicode;
    private final int textChannelId;

    // Constructors:
    public UpdateTextChannelOfServerFromMainServer(int serverUnicode, int textChannelId) {
        this.serverUnicode = serverUnicode;
        this.textChannelId = textChannelId;
    }

    // Methods:
    @Override
    public Object act() throws IOException {
        synchronized (MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelId)) {
            return MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelId);
        }
    }
}
