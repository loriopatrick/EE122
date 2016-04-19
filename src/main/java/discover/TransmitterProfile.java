package discover;

import java.util.List;

/**
 * @author plorio
 */
public class TransmitterProfile {
    private final List<ReceiverChange> events;
    private final int receiver;

    public TransmitterProfile(List<ReceiverChange> events) {
        this.events = events;
        this.receiver = events.get(0).getReceiver();
    }

    public List<ReceiverChange> getEvents() {
        return events;
    }

    public int getFirstReceiver() {
        return receiver;
    }
}
