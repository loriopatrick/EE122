package discover;

import models.Grid;
import models.Receiver;
import models.Signal;
import models.SignalEngine;

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

    public NetworkAnalyzer(Receiver[] receivers) {
        this.receivers = receivers;
        rcvSignals = new float[receivers.length];
        signals = new ArrayList<>();

        Grid grid = new Grid();
        for (int i = 0; i < receivers.length; i++) {
            grid.addReceiver(receivers[i]);
            rcvSignals[i] = receivers[i].signal;
        }
        engine = new SignalEngine(grid);
    }

    public void addTimedSignal(float x, float y, float energy, float time) {

    }

    public void update() {
        // engine.update();

        for (int i = 0; i < receivers.length; i++) {
            if (receivers[i].signal != rcvSignals[i]) {
                change(receivers[i], rcvSignals[i], receivers[i].signal);
                rcvSignals[i] = receivers[i].signal;
            }
        }
    }

    public abstract void change(Receiver receiver, float before, float after);
}
