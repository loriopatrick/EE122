import run.SystemRunner;

/**
 * @author plorio
 */
public class Main {
    public static void main(String[] args) {
        SystemRunner systemRunner = new SystemRunner();

        for (int i = 0; i < 50000; i++) {
            systemRunner.tick();
        }
    }
}
