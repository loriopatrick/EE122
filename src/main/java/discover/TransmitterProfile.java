package discover;

import engine.Receiver;

import java.util.List;

/**
 * @author plorio
 */
public class TransmitterProfile {
    private final List<ChangeEvent> events;
    private final int receiver;

    public TransmitterProfile(List<ChangeEvent> events) {
        this.events = events;
        this.receiver = events.get(0).getReceiver();
    }

    public List<ChangeEvent> getEvents() {
        return events;
    }

    public int getFirstReceiver() {
        return receiver;
    }
}
