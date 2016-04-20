package run;


import engine.Position;
import engine.Signal;

import javax.swing.*;
import java.awt.*;

/*
 * Created by mia on 4/19/16.
 */
public class GraphPanel extends JPanel{

    public Snapshot snapshot;
    public long ticks;
    public int xMax;
    public int yMax;
    public int scaleValue;
    public GraphPanel(){
        this(1000, 1000, 1000, 100);
    }

    public GraphPanel(long ticksPerSecond, int xMaximum, int yMaximum, int scaleVal){
        this.ticks=ticksPerSecond;
        this.xMax=xMaximum;
        this.yMax=yMaximum;
        this.scaleValue = scaleVal;
        setBackground(Color.WHITE);
        setVisible(true);
        this.snapshot=null;

    }

    public void updateSnapshot(Snapshot s){
        this.snapshot = s;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        this.drawGraph(g2);
    }

    private void drawGraph(Graphics2D g){
        g.setBackground(Color.WHITE);
        drawTick(g);
        drawTransmitters(g);
        drawReceivers(g);
        drawSignals(g);
    }

    private void drawTick(Graphics2D g){
        g.setColor(Color.BLACK);
        g.drawString("Current tick: "+Long.toString(this.snapshot.currentTick),10,10);
    }

    private void drawTransmitters(Graphics2D g){
        for(Position p: this.snapshot.transmitters){
            int x = scaleX(p.getX());
            int y = scaleY(p.getY());
            g.setColor(Color.GREEN);
            g.fillOval(x,y,10,10);
        }
    }

    private void drawReceivers(Graphics2D g){
        for(Position p : this.snapshot.receivers){
            int x = scaleX(p.getX());
            int y = scaleY(p.getY());
            g.setColor(Color.BLUE);
            g.fillOval(x,y,10,10);
        }
    }

    private void drawSignals(Graphics2D g){
        for(Signal s : this.snapshot.signals){
            if(s.isAlive()){
                int x = scaleX(s.getX());
                int y = scaleY(s.getY());
                int dist = scaleDistance(distance(s.getPropagationSpeed(),this.snapshot.currentTick));
                g.setColor(Color.BLACK);
                g.drawOval(x-dist,y-dist,dist*2,dist*2);
            }
        }
    }

    private int scaleDistance(long x){
        int scaledX = Math.toIntExact(x);
        scaledX = scaledX/this.scaleValue;
        return scaledX;
    }

    private int scaleX(long x){
        int scaledX = Math.toIntExact(x);
        scaledX = scaledX/this.scaleValue;
        scaledX = scaledX +this.xMax/2;
        return scaledX;
    }

    private int scaleY(long y){
        int scaledY = Math.toIntExact(y);
        scaledY = scaledY/this.scaleValue;
        scaledY = scaledY+this.yMax/2;
        return scaledY;

    }

    private long distance(long speed, long ticks) {
        return speed * ticks / this.ticks;
    }

}
