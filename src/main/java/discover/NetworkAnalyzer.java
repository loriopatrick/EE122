package discover;

import engine.Grid;
import engine.Receiver;
import engine.Signal;
import engine.SignalEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author plorio
 */
public abstract class NetworkAnalyzer {
    private final Receiver[] receivers;
    private final float[] rcvSignals;

    private final List<Signal> signals;

    private final SignalEngine engine;

    public NetworkAnalyzer(long ticksPerSecond, Receiver[] receivers) {
        this.receivers = receivers;
        rcvSignals = new float[receivers.length];
        signals = new ArrayList<>();

        Grid grid = new Grid();
        for (int i = 0; i < receivers.length; i++) {
            grid.addReceiver(receivers[i]);
            rcvSignals[i] = receivers[i].getSignal();
        }
        engine = new SignalEngine(ticksPerSecond, grid);
    }

    public void addTimedSignal(float x, float y, float energy, float time) {

    }

    public void update() {
        // engine.update();

        for (int i = 0; i < receivers.length; i++) {
            if (receivers[i].getSignal() != rcvSignals[i]) {
                change(receivers[i], rcvSignals[i], receivers[i].getSignal());
                rcvSignals[i] = receivers[i].getSignal();
            }
        }
    }

    public abstract void change(Receiver receiver, float before, float after);
}
