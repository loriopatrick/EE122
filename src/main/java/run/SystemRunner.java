package run;

import discover.*;
import engine.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author plorio
 */
public class SystemRunner {
    public static final long SAMPLE_RATE = 100;

    private final int rCount;
    private final int tCount;

    private final Random random;
    private final Receiver[] receivers;
    private final Transmitter[] transmitters;
    private final TransmitterProfile[] profiles;
    private final SignalEngine engine;
    private final NetworkAnalyzer analyzer;
    private final Snapshot snapshot;
    private final OptionDecoder decoder;
    private final long[] decoded;

    public SystemRunner(int rCount, int tCount) {
        this.rCount = rCount;
        this.tCount = tCount;
        random = new Random(1024);
        receivers = buildReceivers();
        transmitters = buildTransmitters();
        profiles = buildProfiles();
        engine = new SignalEngine(SAMPLE_RATE, new Grid(receivers));
        analyzer = new NetworkAnalyzer(engine);
        snapshot = new Snapshot(receivers, transmitters, engine.getSignals(), 10000);
        decoder = new OptionDecoder(profiles);
        decoded = new long[transmitters.length];
    }

    public int getActiveProfiles() {
        return decoder.getActiveProfileCount();
    }

    public Snapshot tick(boolean transmit) {
        if (transmit) {
            for (Transmitter transmitter : transmitters) {
                if (engine.getCurrentTick() % 2 == 0 && random.nextDouble() > 0.5) {
                    transmitter.setActive(!transmitter.isActive());
                }
                transmitter.update(engine);
            }
        }

        engine.update();
        ReceiverChange[] changes = analyzer.getChanges();

        if (decoder.processChanges(engine.getCurrentTick(), changes)) {
            throw new RuntimeException("Decoder error at " + engine.getCurrentTick());
        }

        List<SignalEvent> signalEvents = decoder.takeEvents();
        snapshot.decodedTicks.push(decoder.getLastDecodedTick());
        snapshot.ticks.push(engine.getCurrentTick());
        for (SignalEvent signalEvent : signalEvents) {
            decoded[signalEvent.getTransmitter()] = signalEvent.isOn() ? 1 : 0;
        }

        snapshot.currentTick = engine.getCurrentTick();
        snapshot.decodedTick = engine.getCurrentTick() - decoder.getDecoderLag();
        for (int i = 0; i < snapshot.receiversHistory.length; i++) {
            snapshot.receiversHistory[i].push(receivers[i].getSignal());
        }
        for (int i = 0; i < snapshot.transmittersHistory.length; i++) {
            snapshot.transmittersHistory[i].push(transmitters[i].isActive() ? 1 : 0);
        }
        for (int i = 0; i < snapshot.decodedTransmitterHistory.length; i++) {
            snapshot.decodedTransmitterHistory[i].push(decoded[i]);
        }
        return snapshot;
    }

    private TransmitterProfile[] buildProfiles() {
        SignalEngine engine = new SignalEngine(SAMPLE_RATE, new Grid(buildReceivers()));

        Transmitter[] transmitters = buildTransmitters();
        TransmitterProfile[] profiles = new TransmitterProfile[transmitters.length];
        for (int i = 0; i < profiles.length; i++) {
            profiles[i] = new TransmitterProfile(getEffect(engine, transmitters[i]));
        }

        return profiles;
    }

    private Receiver[] buildReceivers() {
        Receiver[] receivers = new Receiver[rCount];
        int sep = tCount * 10 / rCount;
        for (int i = 0; i < receivers.length; i++) {
            receivers[i] = new Receiver(i, MathUtil.inMeters(0), MathUtil.inMeters((i+0.5) * sep - 0.5));
        }
        return receivers;
    }

    private Transmitter[] buildTransmitters() {
        Random rand = new Random(100);
        Transmitter[] transmitters = new Transmitter[tCount];
        for (int i = 0; i < transmitters.length; i++) {
            transmitters[i] = new Transmitter(MathUtil.inMeters(10), MathUtil.inJoules(5),
                    MathUtil.inMeters(5), MathUtil.inMeters((i + rand.nextDouble() * 0.75) * 10));
        }
        return transmitters;
    }

    private List<ReceiverChange> getEffect(SignalEngine engine, Transmitter transmitter) {
        int receivers = engine.getGrid().getReceivers().length;
        List<ReceiverChange> receiverChanges = new ArrayList<>(receivers);

        long startTick = engine.getCurrentTick();

        // collect events
        NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer(engine);
        transmitter.setActive(true);
        while (receiverChanges.size() < receivers) {
            transmitter.update(engine);
            engine.update();
            for (ReceiverChange receiverChange : networkAnalyzer.getChanges()) {
                if (receiverChange != null) {
                    receiverChanges.add(receiverChange);
                }
            }
        }

        for (int i = 0; i < receiverChanges.size(); i++) {
            ReceiverChange evt = receiverChanges.get(i);
            receiverChanges.set(i, new ReceiverChange(evt.getReceiver(), evt.getDelta(),
                    evt.getDelta(), evt.getTick() - startTick));
        }

        Collections.sort(receiverChanges);
        return receiverChanges;
    }
}
