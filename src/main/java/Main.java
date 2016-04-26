import discover.OptionDecoder;
import run.SystemRunner;

/**
 * @author plorio
 */
public class Main {
    public static void main(String[] args) {

        int last = 0;
        int countSinceOne = 0;
        int scale = 1;
        int t = 1;
        for (int r = 2; r < 100; r++) {
            for (; t < 100; t++) {
                boolean failed = false;

                OptionDecoder.ResetActiveOptions();
                System.out.print("Try " + t + " transmitter : " + r + " receivers ");
                SystemRunner systemRunner = new SystemRunner(r, t);
                for (int j = 0; j < 50000; j++) {
                    if (OptionDecoder.GetActiveOptions() != last) {
                        scale = OptionDecoder.GetActiveOptions() / (last + 1);
                        last = OptionDecoder.GetActiveOptions();
                        if (last == 1) {
                            countSinceOne = 0;
                        } else {
                            countSinceOne++;
                        }
                        System.out.print(last + ",");
                    } else if (j % 10000 == 0) {
                        System.out.print(OptionDecoder.GetActiveOptions() + ",");
                    }

                    if (countSinceOne > 20 || scale > 10 || last > 10000) {
                        failed = true;
                        break;
                    }
                    systemRunner.tick();
                }

                if (failed) {
                    System.out.println(" FAIL");
                    break;
                } else {
                    System.out.println(" SUCCESS");
                }
            }
        }
    }
}
