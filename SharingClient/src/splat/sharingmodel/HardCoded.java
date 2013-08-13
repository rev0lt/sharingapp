package splat.sharingmodel;

public class HardCoded {
    public static final int[] avatars = {1, 2};

    public static int getAvatar(int charID) {
        if (charID >= 0 && charID < avatars.length)
            return avatars[charID];
        else
            return -1;
    }
}
