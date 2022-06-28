package discord;

import java.io.IOException;

public class UpdateTextChannelOfServerOnMainServer extends Action{
    // Fields:
    private final TextChannel updatedTextChannel;
    private final int serverUnicode;
    private final int textChannelId;

    // Constructors:
    public UpdateTextChannelOfServerOnMainServer(TextChannel updatedTextChannel) {
        this.updatedTextChannel = updatedTextChannel;
        serverUnicode = updatedTextChannel.getServerUnicode();
        textChannelId = updatedTextChannel.getId();
    }

    // Methods:
    @Override
    public Object act() throws IOException {
        synchronized (MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelId)) {
            MainServer.getServers().get(serverUnicode).getTextChannels().set(textChannelId, updatedTextChannel);
            MainServer.updateDatabase(MainServer.getServers().get(serverUnicode));
        }
        return true;
    }
}
