package engine;

/**
 * @author plorio
 */
public class Receiver implements Position {
    private final long x;
    private final long y;

    private long signal;

    public Receiver(long x, long y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public long getX() {
        return x;
    }

    @Override
    public long getY() {
        return y;
    }

    public long getSignal() {
        return signal;
    }

    public void updateSignal(long delta) {
        signal += delta;
    }

    @Override
    public String toString() {
        return "Receiver{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
