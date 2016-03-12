package engine;

/**
 * @author plorio
 */
public class TimedSignal extends Signal {
    private long lifetime;

    public TimedSignal(long propagationSpeed, long x, long y, long energy, long lifetimeInTicks) {
        super(propagationSpeed, x, y, energy);
        this.lifetime = lifetimeInTicks;
    }

    @Override
    public void update() {
        alive = lifetime > 0;
        lifetime -= 1;
        super.update();
    }
}
