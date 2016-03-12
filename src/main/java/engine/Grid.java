package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author plorio
 */
public class Grid {
    private List<Receiver> receivers;

    public Grid() {
        receivers = new ArrayList<>();
    }

    /***
     * Units are in meters with #Constants.METER_ACCURACY
     *
     * Returns all receivers that are in within the donut with center (x,y) and
     * interior start inclusive and exterior end exclusive.
     *
     * @param center center of donut
     * @param start from center
     * @param end from center
     * @return All receivers in donut
     */
    public List<Receiver> radialQuery(Position center, long start, long end) {
        final long startSquared = MathUtil.square(start);
        final long endSquared = MathUtil.square(end);

        return receivers.stream().filter((rcv) -> {
            long distSquared = MathUtil.square(center.getX() - rcv.getX()) +
                    MathUtil.square(center.getY() - rcv.getY());
            return distSquared >= startSquared && distSquared < endSquared;
        }).collect(Collectors.toList());
    }

    public Receiver addReceiver(Receiver rcv) {
        receivers.add(rcv);
        return rcv;
    }
}
