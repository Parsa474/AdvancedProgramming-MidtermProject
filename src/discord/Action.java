package discord;

import java.io.IOException;
import java.io.Serializable;

public abstract class Action implements Serializable {
    public abstract Object act() throws IOException;
}
