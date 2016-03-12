package engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author plorio
 */
public class SignalEngine {
    private final long ticksPerSecond;
    private final Grid grid;
    private final List<Signal> signals;

    public SignalEngine(long ticksPerSecond, Grid grid) {
        this.ticksPerSecond = ticksPerSecond;
        this.grid = grid;
        this.signals = new ArrayList<>();
    }

    private long currentTick;

    public void update() {
        currentTick++;
        for (Signal signal : signals) {
            long originalStartLifetime = signal.getLifetimeStart();
            long originalEndLifetime = signal.getLifetimeEnd();
            signal.update();

            List<Receiver> receiversNoLongerInRange = grid.radialQuery(
                    signal,
                    distance(signal.getPropagationSpeed(), originalEndLifetime),
                    distance(signal.getPropagationSpeed(), signal.getLifetimeEnd())
            );

            List<Receiver> receiversNowInRange = grid.radialQuery(
                    signal,
                    distance(signal.getPropagationSpeed(), originalStartLifetime),
                    distance(signal.getPropagationSpeed(), signal.getLifetimeStart())
            );

            for (Receiver receiver : receiversNoLongerInRange) {
                receiver.updateSignal(-signal.getStrength(receiver));
            }

            for (Receiver receiver : receiversNowInRange) {
                receiver.updateSignal(signal.getStrength(receiver));
            }
        }
    }

    /**
     * @param speed in #Constants.METERS_ACCURACY
     * @param ticks in steps
     * @return distance in #Constants.METERS_ACCURACY
     */
    private long distance(long speed, long ticks) {
        return speed * ticks / ticksPerSecond;
    }

    public Signal addSignal(Signal signal) {
        signals.add(signal);
        return signal;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public long getCurrentTime() {
        return (currentTick * Constants.SECOND_ACCURACY) / ticksPerSecond;
    }
}

