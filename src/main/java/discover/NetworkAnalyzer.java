package discover;

import engine.Receiver;
import engine.SignalEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author plorio
 */
public class NetworkAnalyzer {
    private final SignalEngine engine;
    private final Receiver[] receivers;
    private final long[] lastSignals;

    public NetworkAnalyzer(SignalEngine engine) {
        this.engine = engine;
        receivers = engine.getGrid().getReceivers();
        lastSignals = new long[receivers.length];

        for (int i = 0; i < receivers.length; i++) {
            lastSignals[i] = receivers[i].getSignal();
        }
    }

    public List<ChangeEvent> getChanges() {
        ArrayList<ChangeEvent> events = new ArrayList<>();
        for (int i = 0; i < receivers.length; i++) {
            long signal = receivers[i].getSignal();
            if (signal != lastSignals[i]) {
                events.add(new ChangeEvent(
                        receivers[i],
                        signal - lastSignals[i],
                        signal,
                        engine.getCurrentTick()
                ));
                lastSignals[i] = signal;
            }
        }
        return events;
    }
}
