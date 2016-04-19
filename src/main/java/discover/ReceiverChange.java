package discover;

/**
 * @author plorio
 */
public class ReceiverChange implements Comparable<ReceiverChange> {
    private final int receiver;
    private final long delta;
    private final long finalPower;
    private final long tick;

    public ReceiverChange(int receiver, long delta, long finalPower, long tick) {
        this.receiver = receiver;
        this.delta = delta;
        this.finalPower = finalPower;
        this.tick = tick;
    }

    public int getReceiver() {
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
        return "ReceiverChange{" +
                "receiver=" + receiver +
                ", delta=" + delta +
                ", finalPower=" + finalPower +
                ", tick=" + tick +
                '}';
    }

    @Override
    public int compareTo(ReceiverChange o) {
        return Long.compare(tick, o.tick);
    }
}
