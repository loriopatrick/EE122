import discover.ChangeEvent;
import discover.NetworkAnalyzer;
import discover.TransmitterProfile;
import engine.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * @author plorio
 */
public class Main {
    public static void main(String[] args) {
        Receiver[] receivers = {
                new Receiver(-MathUtil.inMeters(50), MathUtil.inMeters(0)),
                new Receiver(MathUtil.inMeters(30), MathUtil.inMeters(30)),
                new Receiver(MathUtil.inMeters(30), MathUtil.inMeters(-30))
        };

        Transmitter[] transmitters = {
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(50),
                        MathUtil.inMeters(5), MathUtil.inMeters(5)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(10),
                        MathUtil.inMeters(20), MathUtil.inMeters(20)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(30),
                        MathUtil.inMeters(30), MathUtil.inMeters(50)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(30),
                        MathUtil.inMeters(15), -MathUtil.inMeters(15)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(60),
                        MathUtil.inMeters(0), MathUtil.inMeters(0)),
        };

        SignalEngine engine = new SignalEngine(100, new Grid(receivers));

        TransmitterProfile[] profiles = new TransmitterProfile[transmitters.length];
        for (int i = 0; i < profiles.length; i++) {
            profiles[i] = new TransmitterProfile(getEffect(engine, transmitters[i]));
            System.out.println(i + " :: " + profiles[i].getEvents());
        }

        NetworkAnalyzer analyzer = new NetworkAnalyzer(engine);
        for (Transmitter transmitter : transmitters) {
            transmitter.setActive(false);
        }

        for (Receiver receiver : receivers) {
            receiver.updateSignal(-receiver.getSignal());
        }

        engine = new SignalEngine(100, new Grid(receivers));

        for (int i = 0; i < 600; i++) {
            for (Transmitter transmitter : transmitters) {
                if (Math.random() > 0.9 && i % 5 == 0) {
                    transmitter.setActive(!transmitter.isActive());
                }
                transmitter.update(engine);
            }

            engine.update();
            List<ChangeEvent> changes = analyzer.getChanges();
            if (changes.size() > 0) {
                System.out.println("TICK: " + engine.getCurrentTick());
                changes.forEach(System.out::println);
            }
        }

        System.exit(0);

        boolean[] decode = new boolean[transmitters.length];
//        TreeMap<Long, >

        boolean run = true;
//        while (run) {
//            engine.update();
//            List<ChangeEvent> changes = analyzer.getChanges();
//        }

        /*
        (event) -> {
            for (TransmitterProfile profile : profiles) {
                if (profile.getFirstReceiver() == event.getReceiver()) {
                    for (ChangeEvent changeEvent : profile.getEvents()) {
                        eventLoop.onTick(engine.getCurrentTick() + changeEvent.getTick(), ()->{

                        });
                    }
                }
            }
        }
         */

        for (int i = 0; i < 100000; i++) {
            for (Transmitter transmitter : transmitters) {
                transmitter.setActive(Math.random() > 0.5);
                transmitter.update(engine);
            }
            engine.update();
            analyzer.getChanges();
        }
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
