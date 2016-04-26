package run;

import engine.Position;
import engine.Signal;

import java.util.List;

/**
 * @author plorio
 */
public class Snapshot {
    public long currentTick;
    public long decodedTick;
    public final Position[] receivers;
    public final Position[] transmitters;
    public final List<Signal> signals;
    public final CircleBuffer[] transmittersHistory;
    public final CircleBuffer[] receiversHistory;
    public final CircleBuffer[] decodedTransmitterHistory;
    public final CircleBuffer ticks;
    public final CircleBuffer decodedTicks;

    public Snapshot(Position[] receivers, Position[] transmitters, List<Signal> signals, int historySize) {
        this.receivers = receivers;
        this.transmitters = transmitters;
        this.signals = signals;

        transmittersHistory = new CircleBuffer[transmitters.length];
        for (int i = 0; i < transmittersHistory.length; i++) {
            transmittersHistory[i] = new CircleBuffer(historySize);
        }
        receiversHistory = new CircleBuffer[receivers.length];
        for (int i = 0; i < receiversHistory.length; i++) {
            receiversHistory[i] = new CircleBuffer(historySize);
        }
        decodedTransmitterHistory = new CircleBuffer[transmitters.length];
        for (int i = 0; i < decodedTransmitterHistory.length; i++) {
            decodedTransmitterHistory[i] = new CircleBuffer(historySize);
        }
        ticks = new CircleBuffer(historySize);
        decodedTicks = new CircleBuffer(historySize);
    }

    public static class CircleBuffer {
        private final long[] values;
        private int start;

        public CircleBuffer(int size) {
            values = new long[size];
        }

        public int size() {
            return values.length;
        }

        public long get(int index) {
            return values[idx(index)];
        }

        public void push(long value) {
            values[idx(0)] = value;
            start += 1;
        }

        private int idx(int idx) {
            return (start + idx) % values.length;
        }
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "currentTick=" + currentTick +
                ", decodedTick=" + decodedTick +
                '}';
    }
}
