package discover;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author plorio
 */
public class Option {
    private final TreeMap<Long, SignalEvent> signalUpdates;
    private final TransmitterProfile[] profiles;
    private final List<ActiveProfile> activeProfiles;
    private final boolean[] latestStates;

    public Option(TransmitterProfile[] profiles) {
        this.profiles = profiles;
        signalUpdates = new TreeMap<>();
        activeProfiles = new ArrayList<>();
        latestStates = new boolean[profiles.length];
    }

    private long lastProcessedTick = 0;

    public void processEvents(long currentTick, List<ChangeEvent> events) {
        // Filter out profiles we've already discovered
        for (ActiveProfile activeProfile : activeProfiles) {
            if (activeProfile.skippedStage(lastProcessedTick, currentTick)) {
                throw new RuntimeException("Invalid option, skipped stage");
            }

            long scale = activeProfile.invert ? -1 : 1;
            ChangeEvent expected = activeProfile.getStage(currentTick);
            if (expected != null) {
                List<ChangeEvent> cleanedEvents = new ArrayList<>(events.size());
                for (ChangeEvent event : events) {
                    if (event.getReceiver() == expected.getReceiver()) {
                        if (event.getDelta() == expected.getDelta() * scale) {
                            continue;
                        }
                        if (!activeProfile.invert && event.getFinalPower() < expected.getDelta()) {
                            throw new RuntimeException("Invalid option, final power not possible");
                        }
                        cleanedEvents.add(new ChangeEvent(
                                event.getReceiver(),
                                event.getDelta() - expected.getDelta() * scale,
                                event.getFinalPower() - expected.getDelta() * scale,
                                expected.getTick()
                        ));
                    } else {
                        cleanedEvents.add(event);
                    }
                }
                events = cleanedEvents;
            }
        }



        for (ChangeEvent event : events) {
            NumberSum<TransmitterProfile> profileOptions = new NumberSum<>();
            for (int i = 0; i < profiles.length; i++) {
                if (profiles[i].getFirstReceiver() == event.getReceiver()) {
                    int scale = latestStates[i] ? -1 : 1;
                    profileOptions.add(scale * profiles[i].getEvents().get(0).getDelta(), profiles[i]);
                }
            }

            List<List<TransmitterProfile>> options = profileOptions.options(event.getDelta());

        }
    }

    private static class ActiveProfile {
        final TransmitterProfile profile;
        final long discoveryTick;
        final long startTick;
        final boolean invert;

        public ActiveProfile(TransmitterProfile profile, long discoveryTick, boolean invert) {
            this.profile = profile;
            this.discoveryTick = discoveryTick;
            this.invert = invert;
            startTick = discoveryTick - profile.getEvents().get(0).getTick();
        }

        public ChangeEvent getStage(long tick) {
            tick -= startTick;
            for (ChangeEvent changeEvent : profile.getEvents()) {
                if (changeEvent.getTick() == tick) {
                    return changeEvent;
                }
                if (changeEvent.getTick() > tick) {
                    return null;
                }
            }
            return null;
        }

        public boolean skippedStage(long lastTick, long currentTick) {
            for (ChangeEvent changeEvent : profile.getEvents()) {
                if (changeEvent.getTick() > lastTick && changeEvent.getTick() < currentTick) {
                    return true;
                }
            }
            return false;
        }
    }
}
