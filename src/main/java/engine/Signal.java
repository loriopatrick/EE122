package engine;

/**
 * @author plorio
 */
public class Signal implements Position {
    private final long propagationSpeed;
    private final long x;
    private final long y;
    private final long energy;

    public Signal(long x, long y, long energy) {
        this(Constants.SPEED_OF_LIGHT, x, y, energy);
    }

    public Signal(long propagationSpeed, long x, long y, long energy) {
        this.propagationSpeed = propagationSpeed;
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.alive = true;
    }

    protected boolean alive;
    private long lifetimeStart;
    private long lifetimeEnd;

    /**
     * @param position used to calculate distance
     * @return signal strength the sensor at location receives from this signal
     */
    public long getStrength(Position position) {
        long distanceSquared = (MathUtil.square(position.getX() - x) + MathUtil.square(position.getY() - y));
        return energy * Constants.METER_ACCURACY * Constants.METER_ACCURACY / distanceSquared;
    }

    public void update() {
        lifetimeStart++;
        if (!alive) {
            lifetimeEnd++;
        }
    }

    /**
     * @return if the signal is emitting from the source (x,y)
     */
    public boolean isAlive() {
        return alive;
    }

    public void stopSignal() {
        alive = false;
    }

    /**
     * @return the tine since the signal started in ticks
     */
    public long getLifetimeStart() {
        return lifetimeStart;
    }

    /**
     * @return the time since the signal ended in ticks
     */
    public long getLifetimeEnd() {
        return lifetimeEnd;
    }

    /**
     * @return in #Constants.METER_ACCURACY
     */
    public long getPropagationSpeed() {
        return propagationSpeed;
    }

    @Override
    public long getX() {
        return x;
    }

    @Override
    public long getY() {
        return y;
    }
}
