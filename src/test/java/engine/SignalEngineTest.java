package engine;

import org.junit.Assert;

/**
 * @author plorio
 */
public class SignalEngineTest {

    @org.junit.Test
    public void testUpdate() throws Exception {
        Receiver[] receivers = {
                new Receiver(MathUtil.inMeters(1), MathUtil.inMeters(-1)), // @ 1.4 seconds
                new Receiver(MathUtil.inMeters(-3), MathUtil.inMeters(3)), // @ 4.24 seconds
                new Receiver(MathUtil.inMeters(4.5), MathUtil.inMeters(4.5)), // @ 5.65 seconds
                new Receiver(MathUtil.inMeters(-5), MathUtil.inMeters(-5)) // @ 7.071 seconds
        };

        Grid grid = new Grid(receivers);
        SignalEngine engine = new SignalEngine(1, grid);

        Signal signal = engine.addSignal(new Signal(MathUtil.inMeters(1), 0, 0, MathUtil.inJoules(10000)));

        engine.update();
        Assert.assertEquals("should be at first tick", engine.getCurrentTick(), 1L);
        Assert.assertEquals("should be at 1 second", engine.getCurrentTime(), MathUtil.inSeconds(1));
        assertSignals("signal should have reached a receiver after 1 tick", receivers, 0, 0, 0, 0);

        for (int i = 1; i < 4; i++) {
            engine.update();
            assertSignals("signal should have reached second receiver before 4 seconds", receivers, MathUtil.inJoules(5000), 0, 0, 0);
        }

        engine.update();
        Assert.assertEquals("should be at tick 5", engine.getCurrentTick(), 5L);
        Assert.assertEquals("should be at 5 second", engine.getCurrentTime(), MathUtil.inSeconds(5));
        assertSignals("signal should have reached first two receivers", receivers, MathUtil.inJoules(5000), MathUtil.inJoules(555.555), 0, 0);

        for (int i = 5; i < 6; i++) {
            engine.update();
            assertSignals("signal should have reached first two receivers", receivers,  MathUtil.inJoules(5000), MathUtil.inJoules(555.555), 0, 0);
        }

        engine.update();
        Assert.assertEquals("should be at tick 7", engine.getCurrentTick(), 7L);
        Assert.assertEquals("should be at 7 second", engine.getCurrentTime(), MathUtil.inSeconds(7));
        assertSignals("signal should have reached first three receivers", receivers,  MathUtil.inJoules(5000), MathUtil.inJoules(555.555), MathUtil.inJoules(246.913), 0);

        engine.update();
        Assert.assertEquals("should be at tick 8", engine.getCurrentTick(), 8L);
        Assert.assertEquals("should be at 8 second", engine.getCurrentTime(), MathUtil.inSeconds(8));
        assertSignals("signal should have reached first three receivers", receivers, MathUtil.inJoules(5000), MathUtil.inJoules(555.555), MathUtil.inJoules(246.913), MathUtil.inJoules(200));

        signal.stopSignal();

        while (engine.getCurrentTick() < 14) {
            engine.update();
        }

        assertSignals("signal should have left first two receivers", receivers, 0, 0, MathUtil.inJoules(246.913), MathUtil.inJoules(200));
        engine.update();
        assertSignals("signal should have left first three receivers", receivers, 0, 0, 0, MathUtil.inJoules(200));
    }

    public void assertSignals(String message, Receiver[] receivers, long... signals) {
        long[] actual = new long[receivers.length];
        for (int i = 0; i < receivers.length; i++) {
            actual[i] = receivers[i].getSignal();
        }
        Assert.assertArrayEquals(message, signals, actual);
    }
}