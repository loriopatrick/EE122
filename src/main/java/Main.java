import discover.*;
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

        int last = 445;
        for (int i = 0; i < last; i++) {
            for (int j = 0; j < transmitters.length; j++) {
                Transmitter transmitter = transmitters[j];
                if (rand.nextDouble() > 0.9) {
                    transmitter.setActive(!transmitter.isActive());
                    System.out.println("SET " + new SignalEvent(j, transmitter.isActive(), engine.getCurrentTick()));
                }
                transmitter.update(engine);
            }

            engine.update();
            List<ChangeEvent> changes = analyzer.getChanges();
            if (!decoder.processEvents(engine.getCurrentTick(), changes)) {
                throw new RuntimeException("BAD AND SAD");
            }
        }

        for (int i = 0; i < 5000; i++) {
            engine.update();
            List<ChangeEvent> changes = analyzer.getChanges();
            if (engine.getCurrentTick() == 1245) {
                System.out.println("HERE");
            }
            if (!decoder.processEvents(engine.getCurrentTick(), changes)) {
                throw new RuntimeException("BAD AND SAD");
            }
        }
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
        };
    }

    public static List<ChangeEvent> getEffect(SignalEngine engine, Transmitter transmitter) {
        int receivers = engine.getGrid().getReceivers().length;
        List<ChangeEvent> changeEvents = new ArrayList<>(receivers);

        long startTick = engine.getCurrentTick();

        // collect events
        NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer(engine);
        transmitter.setActive(true);
        while (changeEvents.size() < receivers) {
            transmitter.update(engine);
            engine.update();
            changeEvents.addAll(networkAnalyzer.getChanges());
        }

        for (int i = 0; i < changeEvents.size(); i++) {
            ChangeEvent evt = changeEvents.get(i);
            changeEvents.set(i, new ChangeEvent(evt.getReceiver(), evt.getDelta(),
                    evt.getDelta(), evt.getTick() - startTick));
        }

        Collections.sort(changeEvents);
        return changeEvents;
    }
}
