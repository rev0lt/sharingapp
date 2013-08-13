package audio;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class FskWriter {

    private static final int AUDIO_SAMPLE_FREQ = 44100;
    private static final int AUDIO_BUFFER_SIZE = 16000;
    private static final int SINE_TABLE_LENGTH = 441;
    private static final int SAMPLE_MAX = 32767;
    private static final int BUFFER_BYTE_SIZE = 4826;
    private static final int BYTES_PER_FRAME = 2;
    private static final int BIT_PERIOD = 816326;
    private static final int PRE_CARRIER_BITS = 50;
    private static final int POST_CARRIER_BITS = 7;
    private static final int FREQ_HIGH = 7350;
    private static final int FREQ_LOW = 4900;
    private static final int SAMPLE_RATE = 44100;
    private static final int SAMPLE_DURATION = 1000000000 / SAMPLE_RATE;
    private static final int TABLE_JUMP_HIGH = FREQ_HIGH * SINE_TABLE_LENGTH / SAMPLE_RATE;
    private static final int TABLE_JUMP_LOW = FREQ_LOW * SINE_TABLE_LENGTH / SAMPLE_RATE;
    private static final double PI = 3.14159; // Purposely not using Math.PI

    private static short[] sineTable = new short[SINE_TABLE_LENGTH];
    private static boolean byteUnderflow = true;
    private static int bitCount = 0;
    private static float bitProgress = 0;
    private static boolean sendCarrier = false;
    private static int bits = 0;
    private static int sineTableIndex = 0;
    private static int value;
    private static boolean valueAnalyzed = false;
    private static boolean sineTableInitialized = false;
    private static AudioTrack audioTrack;

    public static void sendData(final int data) {
        valueAnalyzed = false;
        if (!sineTableInitialized) {
            initializeSineTable();
        }
        writeData(encodeData(data));
    }

    private static void initializeSineTable() {
        for (int i = 0; i < SINE_TABLE_LENGTH; i++) {
            sineTable[i] = (short) (Math.sin(i * 2 * PI / SINE_TABLE_LENGTH) * SAMPLE_MAX);
        }
        sineTableInitialized = true;
    }

    private static void writeData(short[] data) {
    	if (audioTrack == null) {
        audioTrack =
                new AudioTrack(AudioManager.STREAM_MUSIC, AUDIO_SAMPLE_FREQ,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        AUDIO_BUFFER_SIZE, AudioTrack.MODE_STREAM);
    	}
        // aT.setStereoVolume(0, 1f);
        audioTrack.play();
        data = FskUtil.makeWorkWithAudioBufferSize(data);
        audioTrack.write(data, 0, data.length);
    }

    private static short[] encodeData(final int data) {
        value = data;
        short[] soundArray = new short[BUFFER_BYTE_SIZE];
        boolean underflow = false;
        if (bitCount <= 0) {
            underflow = !getNextByte(0);
        }
        for (int i = 0, j = 0; i < BUFFER_BYTE_SIZE; i += BYTES_PER_FRAME, j++) {
            if (bitProgress >= BIT_PERIOD) {
                if (bitCount > 0) {
                    bitCount--;
                    if (!sendCarrier) {
                        bits >>= 1;
                    }
                }
                bitProgress -= BIT_PERIOD;
                if (bitCount <= 0) {
                    underflow = !getNextByte(i);
                }
            }
            if (underflow) {
                soundArray[j] = 0;
            } else {
                sineTableIndex += ((bits & 1) > 0) ? TABLE_JUMP_HIGH : TABLE_JUMP_LOW;
                if (sineTableIndex >= SINE_TABLE_LENGTH) {
                    sineTableIndex -= SINE_TABLE_LENGTH;
                }
                soundArray[j] = sineTable[sineTableIndex];
            }
            if (bitCount > 0) {
                bitProgress += SAMPLE_DURATION;
            }
        }
        return soundArray;
    }

    private static boolean getNextByte(final int index) {
        if (byteUnderflow) {
            if (index == 0) {
                bits = 1;
                bitCount = PRE_CARRIER_BITS;
                sendCarrier = true;
                byteUnderflow = false;
                return true;
            }
        } else {
            if (!valueAnalyzed) {
                valueAnalyzed = true;
                bits = (value << 1) | (0x0200);
                bitCount = 10;
                sendCarrier = false;
                return true;
            } else {
                bits = 1;
                bitCount = POST_CARRIER_BITS;
                sendCarrier = true;
                byteUnderflow = true;
                return true;
            }
        }
        bits = 1;
        return false;
    }
}