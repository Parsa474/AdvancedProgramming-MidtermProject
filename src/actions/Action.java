package actions;

import java.io.IOException;
import java.io.Serializable;

public interface Action extends Serializable {
    Object act() throws IOException;
}
