package splat.sharingmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class State {

    private static State instance;
    
    private short myClientId = -1;

    public static State getInstance() {
        if (instance == null) {
            instance = new State();
        }
        return instance;
    }

    public static void resetAll() {
        instance = null;
    }
}
