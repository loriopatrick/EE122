package engine;

/**
 * @author plorio
 */
public class MathUtil {
    private MathUtil() {
    }

    public static long square(long num) {
        return num * num;
    }

    public static long inMeters(double meters) {
        return Math.round(meters * Constants.METER_ACCURACY);
    }

    public static long inSeconds(double seconds) {
        return Math.round(seconds * Constants.SECOND_ACCURACY);
    }

    public static long inJoules(double joules) {
        return Math.round(joules * Constants.JOULE_ACCURACY);
    }
}
