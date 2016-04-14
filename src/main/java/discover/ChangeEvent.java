package discover;

import engine.Receiver;

/**
 * @author plorio
 */
public class ChangeEvent {
    private final Receiver receiver;
    private final long delta;
    private final long finalPower;
    private final long tick;

    public ChangeEvent(Receiver receiver, long delta, long finalPower, long tick) {
        this.receiver = receiver;
        this.delta = delta;
        this.finalPower = finalPower;
        this.tick = tick;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public long getDelta() {
        return delta;
    }

    public long getFinalPower() {
        return finalPower;
    }

    public long getTick() {
        return tick;
    }

    @Override
    public String toString() {
        return "ChangeEvent{" +
                "receiver=" + receiver +
                ", delta=" + delta +
                ", finalPower=" + finalPower +
                ", tick=" + tick +
                '}';
    }
}
