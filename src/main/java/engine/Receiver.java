package engine;

/**
 * @author plorio
 */
public class Receiver implements Position {
    private final int idx;
    private final long x;
    private final long y;

    private long signal;

    public Receiver(int idx, long x, long y) {
        this.idx = idx;
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
                "idx=" + idx +
                ",x=" + x +
                ", y=" + y +
                '}';
    }
}
