package models;

/**
 * @author plorio
 */
public class Signal {
    public final float x;
    public final float y;
    public final float energy;

    public Signal(float x, float y, float energy) {
        this.x = x;
        this.y = y;
        this.energy = energy;
    }

    // Signal is no longer emitting,
    // however can still be traveling through space
    public boolean alive;
    // Time since the signal first started
    public int lifetimeStart;
    // Time since the signal is no longer alive
    public int lifetimeEnd;

    public double getStrength(Receiver rcv) {
        double distance = Math.sqrt(Math.pow(rcv.x - x, 2) + Math.pow(rcv.y - y, 2));
        return Math.sqrt(distance) * energy;
    }
}
