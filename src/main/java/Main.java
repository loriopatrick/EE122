import run.SystemRunner;

/**
 * @author plorio
 */
public class Main {
    public static void main(String[] args) {

        for (int t = 1; t < 100; t++) {
            for (int r = 1; r < t; r++) {
                SystemRunner systemRunner = new SystemRunner(r, t);
                try {
                    for (int j = 0; j < 5000; j++) {
                        systemRunner.tick();
                    }
                } catch (Exception e) {
                    System.out.println(t + " : " + r);
                    throw e;
                }
            }
        }
    }
}
