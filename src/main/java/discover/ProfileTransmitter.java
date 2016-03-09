package discover;

import models.Receiver;

/**
 * @author plorio
 */
public class ProfileTransmitter {
    private final Receiver[] receivers;
    private final Analyzer analyzer;

    public ProfileTransmitter(Receiver[] receivers) {
        this.receivers = receivers;
        this.analyzer = new Analyzer();
    }

    private int step;
    private float[][] power;

    public float[][] profile(float x, float y, float energy, int steps, int signalLifetime) {
        power = new float[receivers.length][];
        for (int i = 0; i < power.length; i++) {
            power[i] = new float[steps];
        }

        analyzer.addTimedSignal(x, y, energy, signalLifetime);
        for (step = 0; step < power.length; step++) {
            analyzer.update();
        }
        return power;
    }

    private class Analyzer extends NetworkAnalyzer {
        public Analyzer() {
            super(receivers);
        }

        @Override
        public void change(Receiver rcv, float before, float after) {
            int idx;
            for (idx = 0; idx < receivers.length; idx++) {
                if (receivers[idx] == rcv) {
                    break;
                }
            }
            power[idx][step] = after;
        }
    }
}
