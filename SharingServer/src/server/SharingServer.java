package server;

import java.util.HashMap;
import java.util.Timer;

import network.NetworkEvent;
import network.SharingType;
import network.UserData;

public class SharingServer extends Server {

    public static final boolean DEBUG = true;
    public static final int GAME_PORT = 1337;

    public static void main(String[] args) {
        System.out.println("Attempting to start game server");
        SharingServer gameServer = new SharingServer(GAME_PORT, DEBUG);
        gameServer.run();
    }

    public SharingServer(int connectionListenerPort, boolean debug) {
        super(connectionListenerPort, debug);
    }

    @Override
    public void handleEvent(final NetworkEvent e) {
        super.handleEvent(e);
        // get the gameId aka the clientNumber
        short clientId = e.getGameId();
        if (e.getType().equals(SharingType.LOG_IN)) {
            handleLogIn(clientId, e.getData());
        } else if (e.getType().equals(SharingType.NEW_USER)) {
            handleNewUser(clientId, e.getData());
        } else if (e.getType().equals(SharingType.UPDATE_USER)) {
            handleUpdateUser(clientId, e.getData());
        } else if (e.getType().equals(SharingType.HIT)) {
            handleHit(clientId, e.getData());
        } else if (e.getType().equals(SharingType.CONFIRM)) {
            handleConfirm(clientId, e.getData());
        }
        // we shouldn't be receiving any other events
    }

    private void handleLogIn(short senderId, Object data) {
        //data: username, password
        Object[] info = (Object[]) data;
        String username = (String) info[0];
        String password = (String) info[1];
        //confirm in database
        UserData user = new UserData();
        ClientConnection senderClient = confirmedClientConnections.get(senderId);
        senderClient
                .sendEvent(new NetworkEvent(SharingType.LOG_IN, user.serialize()));
    }

    private void handleNewUser(short senderId, Object data) {

    }

    private void handleUpdateUser(short senderId, Object data) {

    }

    private void handleHit(short senderId, Object data) {

    }

    private void handleConfirm(short senderId, Object data) {

    }
}
