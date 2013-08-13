package audio;

import java.util.HashMap;
import java.util.Map.Entry;

import android.annotation.SuppressLint;

public class FskUtil {

    private static final int ADJUSTMENT = 0;
    private static final int NUM_OFFSETS = 34;
    private static final int MESSAGE_SIZE_IN_BITS = 8;
	private static final int SHORT_ARRAY_LENGTH = 8024;
    private static int SAMPLES_PER_BIT = 136;
    private static int SLOTS_PER_BIT = 4;
    private static int WINDOW_SIZE = SAMPLES_PER_BIT / SLOTS_PER_BIT;
    private static int MINIMUM_NPEAKS = 1000;

    public static boolean signalAvailable(short[] sound) {
        int sign = 0;
        int count = 0;
        for (int i = 0; i < sound.length; i++) {
            if (sign == 0 && Math.abs(sound[i]) > PEAK_AMPLITUDE_TRESHOLD) {
                sign = Integer.signum((int) sound[i]);
            } else {
                int newSign = Integer.signum((int) sound[i]);
                if (newSign != sign) {
                    sign = newSign;
                    count++;
                    if (count > MINIMUM_NPEAKS) return true;
                }
            }
        }
        return false;
    }

    public static int processSound(final short[] sound) {
        final int[] potentialNumbers = new int[NUM_OFFSETS];
        for (int i = 0; i < potentialNumbers.length; i++) {
            potentialNumbers[i] = countPeaksAndGetNumber(sound, i);
        }
        return getMode(potentialNumbers) + ADJUSTMENT;
    }
    
	public static short[] makeWorkWithAudioBufferSize(short[] lengthenMe) {
		final short[] zeros = new short[SHORT_ARRAY_LENGTH - lengthenMe.length];
		return concatenateArrays(lengthenMe, zeros);
	}

	public static short[] concatenateArrays(short[] a1, short[] a2) {
		short[] data = new short[a1.length + a2.length];
		for (int i = 0; i < a1.length; i++) {
			data[i] = a1[i];
		}
		for (int i = 0; i < a2.length; i++) {
			data[i + a1.length] = a2[i];
		}
		return data;
	}

    @SuppressLint("UseSparseArrays")
    private static int getMode(int[] potentialNumbers) {
        HashMap<Integer, Integer> numToCount = new HashMap<Integer, Integer>();
        for (final int a : potentialNumbers) {
            numToCount.put(a, numToCount.containsKey(a) ? numToCount.get(a) + 1 : 1);
        }
        int maxCount = 0;
        int mode = -1;
        for (final Entry<Integer, Integer> e : numToCount.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                mode = e.getKey();
            }
        }
        return mode;
    }

    /** How many high bits to see before we decide we need to look for the start bit. */
    private static final int ENOUGH_HIGHS = 15;

    /** The high we're looking at the start of the message. */
    private static final int HIGH = 11;

    /** The lowest number of counts needed to consider this window a high. */
    private static final int LOWEST_HIGH_COUNT = 10;

    public static int countPeaksAndGetNumber(final short[] sound, int offset) {
        boolean lookingForStart = false;
        boolean foundStart = false;
        byte[] bits = new byte[MESSAGE_SIZE_IN_BITS];
        int bitsCollected = 0;
        int highsSeen = 0;
        for (int i = 0; i < sound.length / WINDOW_SIZE; i++) {
            final int currentCount =
                    countPeaks(sound, i * WINDOW_SIZE + offset,
                            Math.min((i + 1) * WINDOW_SIZE + offset, sound.length));
            if (!lookingForStart) {
                if (currentCount >= HIGH) highsSeen++;
                if (highsSeen > ENOUGH_HIGHS) lookingForStart = true;
            } else if (!foundStart && currentCount < LOWEST_HIGH_COUNT) {
                foundStart = true;
            } else if (foundStart) {
                bits[bitsCollected++] = currentCount >= LOWEST_HIGH_COUNT ? (byte) 1 : (byte) 0;
                if (bitsCollected == bits.length) {
                    reverseBits(bits);
                    return getNumberFromBitArray(bits);
                }
            }
        }
        return -1;
    }

    private static void reverseBits(byte[] bits) {
        for (int i = 0; i < 4; i++) {
            byte temp = bits[i];
            bits[i] = bits[bits.length - i - 1];
            bits[bits.length - i - 1] = temp;
        }
    }

    private static int getNumberFromBitArray(byte[] array) {
        int result = 0;
        for (int i = 0; i < array.length; i++) {
            result += (array[i] << (array.length - i - 1));
        }
        return result;
    }

    private static final int PEAK_AMPLITUDE_TRESHOLD = 256;

    private static int countPeaks(final short[] array, final int start, final int end) {
        int sign = 0;
        int count = 0;
        for (int i = start; i < end; i++) {
            if (sign == 0 && Math.abs(array[i]) > PEAK_AMPLITUDE_TRESHOLD) {
                sign = Integer.signum((int) array[i]);
            } else {
                int newSign = Integer.signum((int) array[i]);
                if (newSign != sign) {
                    sign = newSign;
                    count++;
                }
            }
        }
        return count;
    }
}
