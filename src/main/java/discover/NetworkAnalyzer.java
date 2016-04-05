package discover;

import engine.Receiver;
import engine.SignalEngine;

/**
 * @author plorio
 */
public class NetworkAnalyzer {
    private final SignalEngine engine;
    private final Listener listener;
    private final Receiver[] receivers;
    private final long[] lastSignals;

    public NetworkAnalyzer(SignalEngine engine, Listener listener) {
        this.engine = engine;
        this.listener = listener;
        receivers = engine.getGrid().getReceivers();
        lastSignals = new long[receivers.length];

        for (int i = 0; i < receivers.length; i++) {
            lastSignals[i] = receivers[i].getSignal();
        }
    }

    public void update() {
        engine.update();
        for (int i = 0; i < receivers.length; i++) {
            long signal = receivers[i].getSignal();
            if (signal != lastSignals[i]) {
                listener.handleChange(new ChangeEvent(
                        receivers[i],
                        signal - lastSignals[i],
                        engine.getCurrentTick()
                ));
                lastSignals[i] = signal;
            }
        }
    }

    public interface Listener {
        void handleChange(ChangeEvent event);
    }
}
