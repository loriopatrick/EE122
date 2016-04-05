package discover;

import engine.Receiver;

/**
 * @author plorio
 */
public class ChangeEvent {
    private final Receiver receiver;
    private final long delta;
    private final long tick;

    public ChangeEvent(Receiver receiver, long delta, long tick) {
        this.receiver = receiver;
        this.delta = delta;
        this.tick = tick;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public long getDelta() {
        return delta;
    }

    public long getTick() {
        return tick;
    }

    @Override
    public String toString() {
        return "ChangeEvent{" +
                "receiver=" + receiver +
                ", delta=" + delta +
                ", tick=" + tick +
                '}';
    }
}
