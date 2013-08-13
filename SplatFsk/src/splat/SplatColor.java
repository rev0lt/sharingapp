package splat;

public class SplatColor {

    public static final SplatColor RAZZMATAZZ = new SplatColor(1, 0, 0);
//    public static final SplatColor PUMPKIN = new SplatColor(255, 117, 24);

    public static final SplatColor BLUE = new SplatColor(0, 0, 1);
    public static final SplatColor PURPLE = new SplatColor(1, 0, 1);
//    public static final SplatColor PINK = new SplatColor(237, 43, 123);
//    public static final SplatColor ORANGE = new SplatColor(235, 85, 35);
    public static final SplatColor YELLOW = new SplatColor(1, 1, 0);
    public static final SplatColor GREEN = new SplatColor(0, 1, 0);
    public static final SplatColor CYAN = new SplatColor(0, 1, 1);
    
    public static final SplatColor WHITE = new SplatColor(1, 1, 1);
    public static final SplatColor OFF = new SplatColor(0, 0, 0);

    public byte rvalue;
    public byte gvalue;
    public byte bvalue;
    public byte ivalue;

    public SplatColor() {
        this((byte) 1, (byte) 1, (byte) 1, (byte) 1);
    }

    public SplatColor(int r, int g, int b) {
        this ((byte) r, (byte) g, (byte) b, (byte) 1);
    }
    
    public SplatColor(byte r, byte g, byte b) {
        this(r, g, b, (byte) 1);
    }
    
    public SplatColor(int r, int g, int b, int i) {
        this ((byte) r, (byte) g, (byte) b, (byte) i);
    }

    /**
     * Constructor for a Splat LED color. Takes values from 0-255.
     * 
     * @param r Red
     * @param g Green
     * @param b Blue
     * @param i Intensity
     */
    public SplatColor(byte r, byte g, byte b, byte i) {
        this.rvalue = r;
        this.gvalue = g;
        this.bvalue = b;
        this.ivalue = i;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SplatColor) {
            SplatColor c = (SplatColor) o;
            return (rvalue == c.rvalue && gvalue == c.gvalue && bvalue == c.bvalue && ivalue == c.ivalue);
        } else {
            return false;
        }
    }


}
