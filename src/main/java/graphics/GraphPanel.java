package graphics;


import engine.Constants;
import engine.Position;
import engine.Signal;
import run.Snapshot;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;

/*
 * Created by mia on 4/19/16.
 */
public class GraphPanel extends JComponent {
    public Snapshot snapshot;
    public long ticks;
    public int xMax;
    public int yMax;
    public int scaleValue;

    public GraphPanel() {
        this(100, 1000, 1000, 100);
    }

    public GraphPanel(long ticksPerSecond, int xMaximum, int yMaximum, int scaleVal) {
        this.ticks = ticksPerSecond;
        this.xMax = xMaximum;
        this.yMax = yMaximum;
        this.scaleValue = scaleVal;
        setBackground(Color.WHITE);
        setVisible(true);
        this.snapshot = null;
    }

    public void updateSnapshot(Snapshot s) {
        this.snapshot = s;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        this.drawGraph(g2);
    }

    private void drawGraph(Graphics2D g) {
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, getWidth(), getHeight());
        drawSignals(g);
        drawTick(g);
        drawTransmitters(g);
        drawReceivers(g);
    }

    private void drawTick(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawString("Current tick: " + Long.toString(this.snapshot.currentTick), 10, 10);
    }

    private void drawTransmitters(Graphics2D g) {
        for (Position p : this.snapshot.transmitters) {
            int x = scaleX(p.getX()) - 5;
            int y = scaleY(p.getY()) - 5;
            g.setColor(Color.GREEN);
            g.fillOval(x, y, 10, 10);
        }
    }

    private void drawReceivers(Graphics2D g) {
        for (Position p : this.snapshot.receivers) {
            int x = scaleX(p.getX()) - 5;
            int y = scaleY(p.getY()) - 5;
            g.setColor(Color.BLUE);
            g.fillOval(x, y, 10, 10);
        }
    }

    private void drawSignals(Graphics2D g) {
        for (Signal s : this.snapshot.signals) {
            int x = scaleX(s.getX());
            int y = scaleY(s.getY());

            int end = scaleDistance(distance(s.getPropagationSpeed(), s.getLifetimeStart()));
            int begin = scaleDistance(distance(s.getPropagationSpeed(), s.getLifetimeEnd()));
            int thickness = end - begin;

            g.setColor(new Color(0, 0, 0, 20));
            g.setStroke(new BasicStroke(thickness));
            end -= thickness / 2;
            g.drawOval(x - end, y - end, end * 2, end * 2);
            g.setClip(null);
        }
    }

    private int scaleDistance(long x) {
        int scaledX = Math.toIntExact(x);
        scaledX = scaledX / this.scaleValue;
        return scaledX;
    }

    private int scaleX(long x) {
        int scaledX = Math.toIntExact(x);
        scaledX = scaledX / this.scaleValue;
        scaledX = scaledX + this.xMax / 2;
        return scaledX;
    }

    private int scaleY(long y) {
        int scaledY = Math.toIntExact(y);
        scaledY = scaledY / this.scaleValue;
        scaledY = scaledY + this.yMax / 2;
        return scaledY;

    }

    private long distance(long speed, long ticks) {
        return speed * ticks / this.ticks;
    }

}
