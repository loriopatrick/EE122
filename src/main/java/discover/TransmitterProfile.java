package discover;

import engine.Receiver;

import java.util.List;

/**
 * @author plorio
 */
public class TransmitterProfile {
    private final List<ChangeEvent> events;
    private final Receiver receiver;

    public TransmitterProfile(List<ChangeEvent> events) {
        this.events = events;
        this.receiver = events.get(0).getReceiver();
    }

    public List<ChangeEvent> getEvents() {
        return events;
    }

    public Receiver getFirstReceiver() {
        return receiver;
    }
}
