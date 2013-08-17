package splat.sharingcontroller;

import network.NetworkEvent;
import serverconnection.PingListener;
import serverconnection.ServerConnection;
import splat.sharingview.EditActivity;

public class EditController {
	EditActivity view;

    public EditController(EditActivity view) {
        this.view = view;
        ServerConnection sc = NetworkController.getServerConnection();
        if (sc != null) {
            // We backed into this activity from the lobby
            sc.setOnReceiveNetworkEventListener(new BackoutListener());
        }
    }
    
    private class BackoutListener extends PingListener {
        @Override
        public void onReceiveNetworkEvent(final ServerConnection serverConnection,
                final NetworkEvent event) {
            super.onReceiveNetworkEvent(serverConnection, event);
            //implement me
        }
    }
}
