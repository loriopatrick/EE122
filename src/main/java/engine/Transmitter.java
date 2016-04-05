package engine;

/**
 * @author plorio
 */
public class Transmitter implements Position {
    private final long propSpeed;
    private final long strength;
    private final long x;
    private final long y;

    public Transmitter(long propSpeed, long strength, long x, long y) {
        this.propSpeed = propSpeed;
        this.strength = strength;
        this.x = x;
        this.y = y;
    }

    private boolean addSignal = false;
    private Signal signal = null;

    public void setActive(boolean active) {
        if (active) {
            if (signal == null) {
                signal = new Signal(propSpeed, x, y, strength);
                addSignal = true;
            }
        } else if (signal != null) {
            signal.stopSignal();
            signal = null;
        }
    }

    public boolean isActive() {
        return signal != null;
    }

    public void update(SignalEngine engine) {
        if (addSignal) {
            engine.addSignal(signal);
            addSignal = false;
        }
    }

    public boolean inEngine() {
        return !addSignal && signal != null;
    }

    @Override
    public long getX() {
        return x;
    }

    @Override
    public long getY() {
        return y;
    }
}
