package discover;

/**
 * @author plorio
 */
public class SignalEvent {
    private final int transmitter;
    private final boolean on;
    private final long tick;

    public SignalEvent(int transmitter, boolean on, long tick) {
        this.transmitter = transmitter;
        this.on = on;
        this.tick = tick;
    }

    public int getTransmitter() {
        return transmitter;
    }

    public boolean isOn() {
        return on;
    }

    public long getTick() {
        return tick;
    }

    @Override
    public String toString() {
        return "SignalEvent{" +
                "transmitter=" + transmitter +
                ", on=" + on +
                ", tick=" + tick +
                '}';
    }
}
