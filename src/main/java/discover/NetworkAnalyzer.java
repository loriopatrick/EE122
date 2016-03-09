package discover;

import models.Grid;
import models.Receiver;
import models.SignalEngine;

/**
 * @author plorio
 */
public abstract class NetworkAnalyzer {
    private final Receiver[] receivers;
    private final float[] signals;

    private final Grid grid;
    private final SignalEngine engine;

    public NetworkAnalyzer(Receiver[] receivers) {
        this.receivers = receivers;
        signals = new float[receivers.length];

        grid = new Grid();
        for (int i = 0; i < receivers.length; i++) {
            grid.addReceiver(receivers[i]);
            signals[i] = receivers[i].signal;
        }
        engine = new SignalEngine();
    }

    public void update() {
        // engine.update();

        for (int i = 0; i < receivers.length; i++) {
            if (receivers[i].signal != signals[i]) {
                change(receivers[i], signals[i], receivers[i].signal);
                signals[i] = receivers[i].signal;
            }
        }
    }

    public abstract void change(Receiver receiver, float before, float after);
}
