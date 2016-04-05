package discover;

import engine.Receiver;
import engine.SignalEngine;

/**
 * @author plorio
 */
public class ProfileTransmitter {
    public static long[][] Record(SignalEngine engine, int steps) {
        Receiver[] receivers = engine.getGrid().getReceivers();
        long[][] powers = new long[receivers.length][steps];
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < receivers.length; ++j) {
                powers[j][i] = receivers[i].getSignal();
            }
            engine.update();
        }
        return powers;
    }

    public static int Lifetime(SignalEngine engine) {
        assert (engine.activeSignals() > 0);
        Receiver[] receivers = engine.getGrid().getReceivers();
        boolean[] received = new boolean[receivers.length];

        int step = 0;
        while (true) {
            boolean done = true;
            for (int i = 0; i < receivers.length; i++) {
                if (!received[i]) {
                    done = false;
                }
                if (receivers[i].getSignal() > 0) {
                    done = false;
                    received[i] = true;
                }
            }
            if (done) {
                return step;
            }
            step++;
            engine.update();
        }
    }
}
