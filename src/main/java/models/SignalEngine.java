package models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author plorio
 */
public class SignalEngine {

    private Grid g;
    private int ticks;
    private List<Signal> signals;

    public SignalEngine(Grid grid){
        this.g = grid;
        this.ticks=0;
        this.signals = new ArrayList<>();
    }

    public void update(){
        this.ticks++;
        for(int i=0; i<this.signals.size(); i++){
            Signal s = this.signals.get(i);
            List<Receiver> oldReceivers = this.g.radialQuery(s.x,s.y,s.getLifetimeStart(),s.getLifetimeEnd());
            s.update();
            signals.set(i,s);
            List<Receiver> newReceivers = this.g.radialQuery(s.x,s.y,s.getLifetimeStart(),s.getLifetimeEnd());
            for(int j=0; j<newReceivers.size(); j++){
                Receiver rcv = newReceivers.get(i);
                rcv.signal+=s.getStrength(rcv);
            }
            for(int j=0; j<oldReceivers.size(); j++){
                Receiver rcv = oldReceivers.get(i);
                rcv.signal-=s.getStrength(rcv);
            }
        }
    }

    public void addSignal(Signal s){
        this.signals.add(s);
    }

    public int getTicks(){
        return this.ticks;
    }
}

