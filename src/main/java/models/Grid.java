package models;

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

    public List<Receiver> radialQuery(double x, double y, double start, double end) {
        return receivers.stream().filter((rcv) -> {
            double dist = Math.sqrt(Math.pow(x - rcv.x, 2) + Math.pow(y - rcv.y, 2));
            return dist >= start && dist < end;
        }).collect(Collectors.toList());
    }
}
