package splat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;
import audio.FskReader;
import audio.FskReader.OnAudioDataReceivedListener;
import audio.FskWriter;

public class Splat implements SplatApi {

    // Debugging
    private static final String LOG_TAG = "Splat";
    private static final boolean DEBUG_FLAG = true;

    // Constants
    private static final long IN_QUEUE_TIMEOUT = 100;
    private static final SplatColor defaultColor = SplatColor.WHITE;

    // Firmware command codes
    private static final byte RGB_LED_COMMAND = 1;
    private static final byte IR_LED_COMMAND = 6;

    // Splat API listeners
    private static OnConnectListener connectListener;
    private static OnDisconnectListener disconnectListener;
    private static OnDataReceiveListener receiveListener;
    private static OnButtonListener buttonListener;

    private static FskReader reader;
    private static LinkedBlockingQueue<Integer> inQueue;
    private static LinkedBlockingQueue<Byte> outQueue;

    private static boolean alive;
    private static Splat instance;

    private static SplatColor currentColor;
    private static boolean rgbLedOn;

    private static final Executor flashQueue = Executors.newSingleThreadExecutor();

    public static Splat getInstance() {
        if (instance == null) {
            instance = new Splat();
        }
        return instance;
    }

    public Splat() {
        inQueue = new LinkedBlockingQueue<Integer>();
        outQueue = new LinkedBlockingQueue<Byte>();
        reader = new FskReader(new OnAudioDataReceivedListener() {
            @Override
            public void onAudioMessageReceived(final int data) {
                inQueue.add(data);
            }
        });
        reader.start();
        alive = true;
        new InConsumer().start();
        new OutConsumer().start();

        currentColor = defaultColor;
        rgbLedOn = false;
    }

    private class InConsumer extends Thread {

        @Override
        public void run() {
            while (alive) {
                byte b;
                try {
                    b = inQueue.take().byteValue();
                    if (receiveListener != null && isOddParity(b)) {
                        if (DEBUG_FLAG)
                            System.out.println("recvMessage: " + Integer.toBinaryString(b));
                        b = (byte) (b >> 1);
                        if (DEBUG_FLAG)
                            System.out.println("recvData: " + Integer.toBinaryString(b));
                        receiveListener.onDataReceive(instance, b);
                    }
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private class OutConsumer extends Thread {
        @Override
        public void run() {
            while (alive) {
                byte message;
                try {
                    message = outQueue.take();
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                    continue;
                }
                FskWriter.sendData(message);
            }
        }
    }

    @Override
    public void setOnConnectListener(OnConnectListener listener) {
        connectListener = listener;
    }

    @Override
    public void setOnDisconnectListener(OnDisconnectListener listener) {
        disconnectListener = listener;
    }

    @Override
    public boolean isConnected() {
        // TODO send a ping to the device and see if it responds
        return alive;
    }

    @Override
    public void sendData(byte data) {
        byte message = (byte) (data << 3);
        message = (byte) (message | IR_LED_COMMAND);
        if (DEBUG_FLAG) System.out.println("sendData: " + Integer.toBinaryString(message));
        sendMessage(message);
    }

    @Override
    public void setOnDataReceiveListener(OnDataReceiveListener listener) {
        receiveListener = listener;
    }


    @Override
    public boolean isLedOn() {
        return rgbLedOn;
    }

    @Override
    public void turnOnLed() {
        sendColorMessage(currentColor);
        rgbLedOn = true;
    }

    @Override
    public void turnOffLed() {
        sendColorMessage(SplatColor.OFF);
        rgbLedOn = false;
    }

    @Override
    public void setColor(SplatColor color) {
        if (rgbLedOn) {
            sendColorMessage(color);
        }
        currentColor = color;
    }

    private void sendColorMessage(SplatColor c) {
        byte color = (byte) ((c.rvalue << 5) | (c.gvalue << 4) | (c.bvalue << 3));
        color = (byte) (color | RGB_LED_COMMAND);
        if (DEBUG_FLAG) System.out.println("sendColor: " + Integer.toBinaryString(color));
        sendMessage(color);
    }

    private void sendMessage(byte message) {
        if (alive) {
            message = (byte) (message << 1);
            message = (isOddParity(message)) ? message : (byte) (message | 1);
            if (DEBUG_FLAG) System.out.println("sendMessage: " + Integer.toBinaryString(message));
            outQueue.add(message);
        }
    }

    @Override
    public void playSound(int resId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void stopSound() {
        // TODO Auto-generated method stub
    }

    @Override
    public void setOnButtonListener(OnButtonListener listener) {
        buttonListener = listener;
    }

    @Override
    public void flashColor(final SplatColor color, final FlashType type) {
        flashQueue.execute(new Runnable() {
            @Override
            public void run() {
                final SplatColor oldColor = Splat.currentColor;
                Splat.this.setColor(color);
                if (!Splat.this.isLedOn()) {
                    Splat.this.turnOnLed();
                }
                try {
                    Thread.sleep(type.onDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Splat.this.turnOffLed();
                try {
                    Thread.sleep(type.offDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Splat.currentColor = oldColor;
                Splat.this.setColor(Splat.currentColor);
                Splat.this.turnOnLed();
            }
        });
    }

    @Override
    public void flashColor(final SplatColor color, final int onDuration, final int offDuration) {
        flashQueue.execute(new Runnable() {
            @Override
            public void run() {
                final SplatColor oldColor = Splat.currentColor;
                Splat.this.setColor(color);
                if (!Splat.this.isLedOn()) {
                    Splat.this.turnOnLed();
                }
                try {
                    Thread.sleep(onDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Splat.this.turnOffLed();
                try {
                    Thread.sleep(offDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Splat.currentColor = oldColor;
                Splat.this.setColor(Splat.currentColor);
                Splat.this.turnOnLed();
            }
        });
    }

    @Override
    public void flashColor(SplatColor color, int onDuration, int offDuration, int numFlashes) {
        for (int i = 0; i < numFlashes; i++) {
            this.flashColor(color, onDuration, offDuration);
        }
    }

    @Override
    public void playColorSequence(SplatColor[] colors, int duration) {
        final SplatColor oldColor = Splat.currentColor;
        for (int i = 0; i < colors.length; i++) {
            Splat.this.playColor(colors[i], duration);
        }
        flashQueue.execute(new Runnable() {
            @Override
            public void run() {
                Splat.this.setColor(oldColor);
            }
        });
    }

    private void playColor(final SplatColor color, final int duration) {
        flashQueue.execute(new Runnable() {
            @Override
            public void run() {
                Splat.this.setColor(color);
                try {
                    Thread.sleep(duration * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void rainbow() {
        // TODO Auto-generated method stub
    }

    /**
     * Calculate parity of a single byte, using Eric Sosman's xor method
     * 
     * @param b byte to test
     * 
     * @return true of if odd parity, false if even parity
     */
    private static boolean isOddParity(final byte b) {
        final int bb = b & 0xff;
        int parity = bb ^ (bb >> 4);
        parity ^= parity >> 2;
        parity ^= parity >> 1;
        return (parity & 1) != 0;
    }

}
