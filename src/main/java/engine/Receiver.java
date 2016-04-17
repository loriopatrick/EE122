package engine;

/**
 * @author plorio
 */
public class Receiver implements Position {
    private final long id;
    private final long x;
    private final long y;

    private long signal;

    public Receiver(long id, long x, long y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public long getId() {
        return id;
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

    public boolean equals(Receiver r) {
        return id == r.id;
    }

    @Override
    public String toString() {
        return "Receiver{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
