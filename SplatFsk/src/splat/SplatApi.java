package splat;

public interface SplatApi {

    // v0.0

    // i/o
    // Can be null if no listener is desired/to unregister a listener
    public void setOnConnectListener(OnConnectListener listener);

    public void setOnDisconnectListener(OnDisconnectListener listener);

    public boolean isConnected();

    // infrared led
    public void sendData(final byte data);

    // infrared sensor
    public void setOnDataReceiveListener(OnDataReceiveListener listener);

    // v0.1

    // rgb led
    public boolean isLedOn();

    public void turnOnLed();

    public void turnOffLed();

    public void setColor(SplatColor color);


    // v0.2

    // speaker
    public void playSound(int resId);

    public void stopSound();


    // v0.3

    // button
    public void setOnButtonListener(OnButtonListener listener);


    // v1.0
    // led stuff
    public enum FlashType {
        FLASH_SLOW(500, 500), FLASH_MEDIUM(300, 300), FLASH_FAST(100, 100), BLINK_SLOW(300, 700), BLINK_MEDIUM(
                150, 450), BLINK_FAST(65, 135);

        public final int onDuration;
        public final int offDuration;

        private FlashType(int onDuration, int offDuration) {
            this.onDuration = onDuration;
            this.offDuration = offDuration;
        }
    }

    public void flashColor(SplatColor color, FlashType type);

    public void flashColor(SplatColor color, int onDuration, int offDuration);

    public void flashColor(SplatColor color, int onDuration, int offDuration, int numFlashes);

    public void playColorSequence(SplatColor[] colors, int duration);

    public void rainbow();
}
