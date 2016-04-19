import discover.NetworkAnalyzer;
import discover.OptionDecoder;
import discover.ReceiverChange;
import discover.TransmitterProfile;
import engine.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author plorio
 */
public class Main {
    public static void main(String[] args) {
        Receiver[] receivers = buildReceivers();
        Transmitter[] transmitters = buildTransmitters();
        TransmitterProfile[] profiles = buildProfiles();

        SignalEngine engine = new SignalEngine(100, new Grid(receivers));
        NetworkAnalyzer analyzer = new NetworkAnalyzer(engine);

        OptionDecoder decoder = new OptionDecoder(profiles);

        Random rand = new Random(3214);

        int last = 100;
        for (int i = 0; i < last; i++) {
            for (int j = 0; j < transmitters.length; j++) {
                Transmitter transmitter = transmitters[j];
                if (rand.nextDouble() > 0.9) {
                    transmitter.setActive(!transmitter.isActive());
                }
                transmitter.update(engine);
            }

            engine.update();
            ReceiverChange[] changes = analyzer.getChanges();
            if (decoder.processChanges(engine.getCurrentTick(), changes)) {
                throw new RuntimeException("BAD AND SAD");
            }
        }

        for (int i = 0; i < 500; i++) {
            engine.update();
            ReceiverChange[] changes = analyzer.getChanges();
            if (decoder.processChanges(engine.getCurrentTick(), changes)) {
                throw new RuntimeException("BAD AND SAD");
            }
        }

        System.out.println(decoder.takeEvents(engine.getCurrentTick()));
    }

    public static TransmitterProfile[] buildProfiles() {
        SignalEngine engine = new SignalEngine(100, new Grid(buildReceivers()));

        Transmitter[] transmitters = buildTransmitters();
        TransmitterProfile[] profiles = new TransmitterProfile[transmitters.length];
        for (int i = 0; i < profiles.length; i++) {
            profiles[i] = new TransmitterProfile(getEffect(engine, transmitters[i]));
        }

        return profiles;
    }

    public static Receiver[] buildReceivers() {
        return new Receiver[]{
                new Receiver(0, -MathUtil.inMeters(0), MathUtil.inMeters(0)),
                new Receiver(1, MathUtil.inMeters(0), MathUtil.inMeters(30)),
                new Receiver(2, MathUtil.inMeters(0), MathUtil.inMeters(-30))
        };
    }

    public static Transmitter[] buildTransmitters() {
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

    public static List<ReceiverChange> getEffect(SignalEngine engine, Transmitter transmitter) {
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
