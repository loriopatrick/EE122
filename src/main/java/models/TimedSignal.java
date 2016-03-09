package models;

/**
 * @author plorio
 */
public class TimedSignal extends Signal {
    private int lifetime;

    public TimedSignal(float x, float y, float energy, int lifetime) {
        super(x, y, energy);
        this.lifetime = lifetime;
    }

    @Override
    public void update() {
        if (lifetime == 0) {
            alive = false;
        }
        lifetime -= 1;
        super.update();
    }
}
