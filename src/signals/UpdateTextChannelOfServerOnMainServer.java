package signals;

import discord.TextChannel;
import mainServer.MainServer;

import java.io.IOException;

public class UpdateTextChannelOfServerOnMainServer implements Action {
    private final int serverUnicode;
    private final int textChannelIndex;
    private final TextChannel updatedTextChannel;

    public UpdateTextChannelOfServerOnMainServer(int serverUnicode, int textChannelIndex, TextChannel updatedTextChannel) {
        this.serverUnicode = serverUnicode;
        this.textChannelIndex = textChannelIndex;
        this.updatedTextChannel = updatedTextChannel;
    }

    @Override
    public Object act() throws IOException {
        synchronized (MainServer.getServers().get(serverUnicode).getTextChannels().get(textChannelIndex)) {
            MainServer.getServers().get(serverUnicode).getTextChannels().set(textChannelIndex, updatedTextChannel);
            return MainServer.updateDatabase(MainServer.getServers().get(serverUnicode));
        }
    }
}