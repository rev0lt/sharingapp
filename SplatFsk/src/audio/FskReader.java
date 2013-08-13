package audio;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/** <uses-permission android:name="android.permission.RECORD_AUDIO" /> */
public class FskReader {

    private static final int AUDIO_SAMPLE_FREQ = 44100;
    private static final int AUDIO_BUFFER_SIZE_IN_BYTES = 16000;
    private static final int AUDIO_BUFFER_SIZE_IN_SHORTS = AUDIO_BUFFER_SIZE_IN_BYTES / 2;
    private static final int MINIMUM_BUFFER = 4;

    private LinkedBlockingQueue<short[]> audioQueue = new LinkedBlockingQueue<short[]>();
    private boolean running;
    private OnAudioDataReceivedListener listener;

    public FskReader(final OnAudioDataReceivedListener listener) {
        this.listener = listener;
    }

    public void start() {
        running = true;
        new Producer().start();
        new Consumer().start();
    }

    public void stop() {
        running = false;
    }

    private class Producer extends Thread {
        @Override
        public void run() {
            AudioRecord aR =
                    new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_FREQ,
                            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                            AUDIO_BUFFER_SIZE_IN_BYTES);
            while (aR.getState() == AudioRecord.STATE_UNINITIALIZED) {
                aR =
                        new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_FREQ,
                                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                                AUDIO_BUFFER_SIZE_IN_BYTES);
            }
            aR.startRecording();

            while (running) {
                final short[] audioData = new short[AUDIO_BUFFER_SIZE_IN_SHORTS];
                aR.read(audioData, 0, AUDIO_BUFFER_SIZE_IN_SHORTS);
                audioQueue.add(audioData);
            }
            aR.stop();
            aR.release();
        }
    }

    private class Consumer extends Thread {
        private boolean signalDetected;
        private short[] header;

        @Override
        public void run() {
            try {
                while (running) {
                    if (signalDetected() && audioQueue.size() >= MINIMUM_BUFFER) {
                        long tic = System.currentTimeMillis();
                        short[] sarray = consumeSoundMessage();
                        tic = System.currentTimeMillis();
                        int derp = FskUtil.processSound(sarray);
                        listener.onAudioMessageReceived(derp);
                        Log.e("fskandui:", System.currentTimeMillis() - tic + "");
                        signalDetected = false;
                    } else {
                        Thread.sleep(50);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private boolean signalDetected() {
            if (!signalDetected) {
                try {
                    short[] in = audioQueue.take();
                    signalDetected = FskUtil.signalAvailable(in);
                    if (signalDetected) header = in;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return signalDetected;
        }

        private short[] consumeSoundMessage() {
            short[] sound = Arrays.copyOf(header, header.length);
            for (int i = 0; i < MINIMUM_BUFFER; i++) {
                short[] addMe = audioQueue.remove();
                final int originalSoundLength = sound.length;
                sound = Arrays.copyOf(sound, sound.length + addMe.length);
                System.arraycopy(addMe, 0, sound, originalSoundLength, addMe.length);
            }
            return sound;
        }
    }

    public interface OnAudioDataReceivedListener {
        public void onAudioMessageReceived(final int data);
    }
}
