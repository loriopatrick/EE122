package graphics;

import run.Snapshot;
import run.SystemRunner;

/**
 * @author plorio
 */
public class GraphicsMain {
    private final SystemRunner runner;
    private final GraphPanel graphPanel;

    public GraphicsMain() {
        runner = new SystemRunner();
        GraphFrame graphFrame = new GraphFrame();
        graphPanel = new GraphPanel();
        graphPanel.updateSnapshot(runner.tick());
        graphFrame.add(graphPanel);
    }

    public void update() {
        Snapshot snapshot = runner.tick();
        graphPanel.updateSnapshot(snapshot);
        graphPanel.paintImmediately(0, 0, graphPanel.xMax, graphPanel.yMax);
    }

    public static void main(String[] args) {
        GraphicsMain graphics = new GraphicsMain();

        while (true) {
            graphics.update();
        }
    }
}
