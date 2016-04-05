import discover.NetworkAnalyzer;
import engine.*;

/**
 * @author plorio
 */
public class Main {
    public static void main(String[] args) {
        Receiver[] receivers = {
                new Receiver(MathUtil.inMeters(30), MathUtil.inMeters(30)),
                new Receiver(-MathUtil.inMeters(50), MathUtil.inMeters(0)),
                new Receiver(MathUtil.inMeters(30), -MathUtil.inMeters(30))
        };

        Transmitter[] transmitters = {
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(50),
                        MathUtil.inMeters(5), MathUtil.inMeters(5)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(10),
                        MathUtil.inMeters(20), MathUtil.inMeters(20)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(30),
                        MathUtil.inMeters(-10), -MathUtil.inMeters(10)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(30),
                        MathUtil.inMeters(15), -MathUtil.inMeters(15)),
                new Transmitter(MathUtil.inSeconds(10), MathUtil.inJoules(60),
                        MathUtil.inMeters(0), MathUtil.inMeters(0)),
        };

        SignalEngine engine = new SignalEngine(10, new Grid(receivers));

        NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer(engine, System.out::println);
        for (int i = 0; i < 1000000; i++) {
            for (Transmitter transmitter : transmitters) {
                if (Math.random() > 0.9) {
                    transmitter.setActive(!transmitter.isActive());
                }
                transmitter.update(engine);
            }
            networkAnalyzer.update();
        }
    }
}
