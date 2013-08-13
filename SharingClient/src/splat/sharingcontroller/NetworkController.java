package splat.sharingcontroller;

import serverconnection.ServerConnection;
import serverconnection.ServerConnection.OnDisconnectListener;
import splat.sharingmodel.Settings;

public class NetworkController {

    private static ServerConnection connection;

    public static void startConnection(final String ip, final int port, final int accountId,
            final OnDisconnectListener onDisconnectListener) {
        connection =
                new ServerConnection(ip, port, accountId, onDisconnectListener, Settings.debug());
    }

    public static ServerConnection getServerConnection() {
        return connection;
    }

    public static void closeConnection() {
        connection.kill();
        connection = null;
    }
}