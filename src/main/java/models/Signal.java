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

    protected boolean alive; // Whether source is still transmitting
    private int lifetimeStart; // Time since the signal first started
    private int lifetimeEnd; // Time since the signal is no longer alive

    public double getStrength(Receiver rcv) {
        double distance = Math.sqrt(Math.pow(rcv.x - x, 2) + Math.pow(rcv.y - y, 2));
        return Math.sqrt(distance) * energy;
    }

    public void update() {
        lifetimeStart++;
        if (!alive) {
            lifetimeEnd++;
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public int getLifetimeStart() {
        return lifetimeStart;
    }

    public int getLifetimeEnd() {
        return lifetimeEnd;
    }
}
