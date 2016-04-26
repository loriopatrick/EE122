package graphics;

import run.Snapshot;
import run.SystemRunner;

import javax.swing.*;

/**
 * @author plorio
 */
public class GraphicsMain {
    private final SystemRunner runner;
    private final GraphPanel graphPanel;

    public GraphicsMain() {
        runner = new SystemRunner(3, 5);
        GraphFrame graphFrame = new GraphFrame();
        graphPanel = new GraphPanel();
        graphPanel.updateSnapshot(runner.tick(true));
        graphFrame.add(graphPanel);
        graphPanel.getInputMap(graphPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "pause");
        graphPanel.getActionMap().put("pause", graphPanel.new Pauser());
    }

    public void update() {
        if(this.graphPanel.paused==false){
            Snapshot snapshot = runner.tick(true);
            graphPanel.updateSnapshot(snapshot);
            if (snapshot.currentTick % 100 == 0) {
                graphPanel.paintImmediately(0, 0, graphPanel.xMax, graphPanel.yMax);
            }
        }
    }

    public static void main(String[] args) {
        GraphicsMain graphics = new GraphicsMain();
        while (true) {
            graphics.update();
        }
    }
}
