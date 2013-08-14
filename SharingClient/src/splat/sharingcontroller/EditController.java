package splat.sharingcontroller;

import android.content.Intent;
import network.NetworkEvent;
import network.SharingType;
import network.UserData;
import serverconnection.HandshakeListener;
import serverconnection.ServerConnection;
import serverconnection.ServerConnection.OnDisconnectListener;
import splat.sharingmodel.State;
import splat.sharingview.EditActivity;
import splat.sharingview.ShootActivity;

public class EditController {
	EditActivity view;

    public EditController(EditActivity view) {
        this.view = view;
        ServerConnection sc = NetworkController.getServerConnection();
        if (sc != null) {
            // We backed into this activity from the lobby
        	sc.setOnReceiveNetworkEventListener(new EditNetworkEventListener());
        	sc.setOnDisconnectListener(new EditDisconnectListener());
        }
    }
    
    private class EditNetworkEventListener extends HandshakeListener {

		@Override
		public void onReceiveNetworkEvent(final ServerConnection sc,
				final NetworkEvent evt) {
			super.onReceiveNetworkEvent(sc, evt);
			if (evt.getType().equals(SharingType.UPDATE_USER)) {
				int success = (Integer) evt.getData();
				if (success == 1) {
					//update state from form
					//go back to previous activity
					sc.setOnReceiveNetworkEventListener(null);
					sc.setOnDisconnectListener(null);
					view.startActivity(new Intent(view, ShootActivity.class));
					view.finish();
				} else {
					//error message
				}
			}
		}
	}
    
    /**
	 * Shows message on disconnect
	 * 
	 * @author Sam
	 */
	public class EditDisconnectListener implements OnDisconnectListener {
		@Override
		public void onDisconnect(ServerConnection connection) {
			view.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// view.showDisconnectDialog();
				}
			});
		}
	}
}
