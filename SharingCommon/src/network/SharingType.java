package network;

import network.NetworkEvent.Type;

public enum SharingType implements Type {
    LOG_IN, NEW_USER, UPDATE_USER, HIT, USER_DATA, CONFIRM;
}
