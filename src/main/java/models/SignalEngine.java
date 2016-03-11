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
            int originalStartLifetime = signal.getLifetimeStart();
            int originalEndLifetime = signal.getLifetimeEnd();
            signal.update();

            List<Receiver> receiversNoLongerInRange = grid.radialQuery(
                    signal.x, signal.y,
                    signal.getPropagationDistance(originalEndLifetime),
                    signal.getPropagationDistance(signal.getLifetimeEnd())
            );

            List<Receiver> receiversNowInRange = grid.radialQuery(
                    signal.x, signal.y,
                    signal.getPropagationDistance(originalStartLifetime),
                    signal.getPropagationDistance(signal.getLifetimeStart())
            );

            for (Receiver receiver : receiversNoLongerInRange) {
                receiver.signal -= signal.getStrength(receiver);
            }

            for (Receiver receiver : receiversNowInRange) {
                receiver.signal += signal.getStrength(receiver);
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

