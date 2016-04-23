package run;

import discover.*;
import engine.*;
import graphics.GraphFrame;
import graphics.GraphPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author plorio
 */
public class SystemRunner {
    private final Random random;
    private final Receiver[] receivers;
    private final Transmitter[] transmitters;
    private final TransmitterProfile[] profiles;
    private final SignalEngine engine;
    private final NetworkAnalyzer analyzer;
    private final Snapshot snapshot;
    private final OptionDecoder decoder;
    private final long[] decoded;

    public SystemRunner() {
        random = new Random(1024);
        receivers = buildReceivers();
        transmitters = buildTransmitters();
        profiles = buildProfiles();
        engine = new SignalEngine(100, new Grid(receivers));
        analyzer = new NetworkAnalyzer(engine);
        snapshot = new Snapshot(receivers, transmitters, engine.getSignals(), 100);
        decoder = new OptionDecoder(profiles);
        decoded = new long[transmitters.length];
    }

    public Snapshot tick() {
        for (Transmitter transmitter : transmitters) {
            if (random.nextDouble() > 0.9) {
                transmitter.setActive(!transmitter.isActive());
            }
            transmitter.update(engine);
        }

        engine.update();
        ReceiverChange[] changes = analyzer.getChanges();

        if (decoder.processChanges(engine.getCurrentTick(), changes)) {
            throw new RuntimeException("Decoder error");
        }

        List<SignalEvent> signalEvents = decoder.takeEvents(engine.getCurrentTick());
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
        SignalEngine engine = new SignalEngine(100, new Grid(buildReceivers()));

        Transmitter[] transmitters = buildTransmitters();
        TransmitterProfile[] profiles = new TransmitterProfile[transmitters.length];
        for (int i = 0; i < profiles.length; i++) {
            profiles[i] = new TransmitterProfile(getEffect(engine, transmitters[i]));
        }

        return profiles;
    }

    private Receiver[] buildReceivers() {
        return new Receiver[]{
                new Receiver(0, -MathUtil.inMeters(0), MathUtil.inMeters(0)),
                new Receiver(1, MathUtil.inMeters(0), MathUtil.inMeters(30)),
                new Receiver(2, MathUtil.inMeters(0), MathUtil.inMeters(-30))
        };
    }

    private Transmitter[] buildTransmitters() {
        return new Transmitter[]{
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(20),
                        MathUtil.inMeters(0), MathUtil.inMeters(5)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(10),
                        MathUtil.inMeters(0), MathUtil.inMeters(20)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(30),
                        MathUtil.inMeters(0), MathUtil.inMeters(50)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(30),
                        MathUtil.inMeters(0), -MathUtil.inMeters(15)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(60),
                        MathUtil.inMeters(0), MathUtil.inMeters(-32)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(5),
                        MathUtil.inMeters(0), MathUtil.inMeters(2)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(5),
                        MathUtil.inMeters(0), MathUtil.inMeters(8)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(5),
                        MathUtil.inMeters(0), MathUtil.inMeters(-12)),
        };
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
