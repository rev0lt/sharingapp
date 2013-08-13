package network;

import network.NetworkEvent.Type;

public enum SharingType implements Type {
    SELECT_CHARACTER, PLAYER_JOINED, PLAYER_LEFT, START_GAME, HIT_BY_POTATO, TOGGLE_POTATO, UPLOAD_SCORE, END_GAME, QUIT_GAME;
}
