package graphics;

import run.Snapshot;
import run.SystemRunner;

/**
 * @author plorio
 */
public class GraphicsMain {
    private final SystemRunner runner;
    private final GraphFrame graphFrame;
    private final GraphPanel graphPanel;

    public GraphicsMain() {
        runner = new SystemRunner();
        graphFrame = new GraphFrame();
        graphPanel = new GraphPanel();
        graphPanel.updateSnapshot(runner.tick());
        graphFrame.add(graphPanel);
    }

    public void update() {
        Snapshot snapshot = runner.tick();
        graphPanel.updateSnapshot(snapshot);
        graphPanel.repaint();
    }

    public static void main(String[] args) {
        GraphicsMain graphics = new GraphicsMain();

        while (true) {
            graphics.update();
        }
    }
}
