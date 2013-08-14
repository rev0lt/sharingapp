package splat.sharingmodel;
import network.UserData;
public class State {

    private static State instance;
    
    private short myClientId = -1;
    private UserData me = new UserData();

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
