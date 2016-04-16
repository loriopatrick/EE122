import discover.ChangeEvent;
import discover.NetworkAnalyzer;
import engine.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        for (Transmitter transmitter : transmitters) {
            System.out.println(transmitter);
            System.out.println(getEffect(engine, transmitter));
        }
    }

    public static List<ChangeEvent> getEffect(SignalEngine engine, Transmitter transmitter) {
        int receivers = engine.getGrid().getReceivers().length;
        List<ChangeEvent> changeEvents = new ArrayList<>(receivers);

        // collect events
        NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer(engine, changeEvents::add);
        transmitter.setActive(true);
        while (changeEvents.size() < receivers) {
            transmitter.update(engine);
            networkAnalyzer.update();
        }

        long minTick = changeEvents.get(0).getTick();
        for (ChangeEvent changeEvent : changeEvents) {
            if (changeEvent.getTick() < minTick) {
                minTick = changeEvent.getTick();
            }
        }

        for (int i = 0; i < changeEvents.size(); i++) {
            ChangeEvent evt = changeEvents.get(i);
            changeEvents.set(i, new ChangeEvent(evt.getReceiver(), evt.getDelta(),
                    evt.getDelta(), evt.getTick() - minTick));
        }

        Collections.sort(changeEvents);
        return changeEvents;
    }
}
