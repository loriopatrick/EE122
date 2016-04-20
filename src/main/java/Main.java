import run.SystemRunner;

/**
 * @author plorio
 */
public class Main {
    public static void main(String[] args) {
        SystemRunner systemRunner = new SystemRunner();
        for (int i = 0; i < 1000; i++) {
            System.out.println(systemRunner.tick());
        }
    }
}
