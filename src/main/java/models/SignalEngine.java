package models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author plorio
 */
public class SignalEngine {
    private final Grid grid;
    private final List<Signal> signals;
    private int ticks;

    public SignalEngine(Grid grid) {
        this.grid = grid;
        this.ticks = 0;
        this.signals = new ArrayList<>();
    }

    public void update() {
        ticks++;
        for (Signal signal : signals) {
            List<Receiver> oldReceivers = this.grid.radialQuery(signal.x, signal.y, signal.getLifetimeStart(), signal.getLifetimeEnd());
            signal.update();
            List<Receiver> newReceivers = this.grid.radialQuery(signal.x, signal.y, signal.getLifetimeStart(), signal.getLifetimeEnd());
            for (Receiver rcv : newReceivers) {
                rcv.signal += signal.getStrength(rcv);
            }
            for (Receiver rcv : oldReceivers) {
                rcv.signal -= signal.getStrength(rcv);
            }
        }
    }

    public void addSignal(Signal s) {
        signals.add(s);
    }

    public int getTicks() {
        return ticks;
    }
}

