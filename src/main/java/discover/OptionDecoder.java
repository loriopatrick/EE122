package discover;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author plorio
 */
public class OptionDecoder {
    private final TreeMap<Long, List<SignalEvent>> signalUpdates;
    private final TransmitterProfile[] profiles;
    private final List<ActiveProfile> activeProfiles;
    private final boolean[] latestStates;

    private final List<OptionDecoder> tangents;

    public OptionDecoder(TransmitterProfile[] profiles) {
        this.profiles = profiles;
        signalUpdates = new TreeMap<>();
        activeProfiles = new ArrayList<>();
        latestStates = new boolean[profiles.length];
        tangents = new ArrayList<>();
    }

    private long lastProcessedTick = 0;

    public boolean processEvents(long currentTick, ChangeEvent[] events) {
        if (tangents.size() > 0) {
            // process all tangents, clean up those that die
            List<OptionDecoder> deadTangents = new ArrayList<>();
            for (OptionDecoder tangent : tangents) {
                if (!tangent.processEvents(currentTick, events)) {
                    deadTangents.add(tangent);
                }
            }
            deadTangents.forEach(tangents::remove);

            // all future possibilities are not possible
            // so this must not be possible
            if (tangents.size() == 0) {
                System.out.println("Removed all tangents");
                return false;
            }

            // merge tangent to this option
            if (tangents.size() == 1) {
                OptionDecoder reality = tangents.get(0);

                // merge signal updates
                reality.signalUpdates.forEach((key, value) -> {
                    List<SignalEvent> signalEvents = signalUpdates.get(key);
                    if (signalEvents == null) {
                        signalUpdates.put(key, value);
                    } else {
                        signalEvents.addAll(value);
                    }
                });

                // merge active profiles
                activeProfiles.clear();
                activeProfiles.addAll(reality.activeProfiles);

                // merge current state
                System.arraycopy(reality.latestStates, 0, latestStates, 0, latestStates.length);
                lastProcessedTick = reality.lastProcessedTick;

                // I am the captain now
                tangents.clear();
            }

            return true;
        }

        // Filter out events from profiles we've already discovered
        events = FilterEvents(lastProcessedTick, currentTick, events, activeProfiles);
        if (events == null) {
            return false;
        }

        activeProfiles.removeIf((p) -> p.isOver(currentTick));
        lastProcessedTick = currentTick;

        return computeAndApplyProfiles(currentTick, events);
    }

    private boolean computeAndApplyProfiles(long tick, ChangeEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            ChangeEvent event = events[i];

            if (event == null) {
                continue;
            }

            // Add all profiles that started with the event's receiver to the query
            NumberSum<Integer> profileOptions = new NumberSum<>();
            for (int j = 0; j < profiles.length; j++) {
                if (profiles[j].getFirstReceiver() == event.getReceiver()) {
                    int scale = latestStates[j] ? -1 : 1;
                    profileOptions.add(scale * profiles[j].getEvents().get(0).getDelta(), j);
                }
            }

            // Determine all possible combinations of profiles that could have created this event
            List<List<Integer>> options = profileOptions.options(event.getDelta());
            if (options.size() == 0) {
                System.out.println("No possible way to create this event");
                return false;
            }

            // Apply the only possible combination
            if (options.size() == 1) {
                List<Integer> profileIndices = options.get(0);

                List<ActiveProfile> addedProfiles = profileIndices.stream()
                        .map(idx -> applyProfile(tick, idx)).collect(Collectors.toList());

                // clean events with the our new understanding of the system
                events = FilterEvents(tick, tick, events, addedProfiles);
                if (events == null) {
                    return false;
                }
            }
            // Split off into tangents for all combination possibilities
            else {
                for (List<Integer> activatedProfiles : options) {
                    OptionDecoder tangent = new OptionDecoder(profiles);
                    tangent.activeProfiles.addAll(activeProfiles);
                    System.arraycopy(latestStates, 0, tangent.latestStates, 0, latestStates.length);
                    tangent.lastProcessedTick = tick;

                    List<ActiveProfile> addedProfiles = activatedProfiles.stream()
                            .map(idx -> tangent.applyProfile(tick, idx)).collect(Collectors.toList());
                    ChangeEvent[] tangentEvents = FilterEvents(tick, tick, events, addedProfiles);
                    if (tangentEvents == null) {
                        return false;
                    }
                    tangent.computeAndApplyProfiles(tick, tangentEvents);
                    tangents.add(tangent);
                }
            }
        }

        return true;
    }

    private static ChangeEvent[] FilterEvents(long previousTick, long tick,
                                                  ChangeEvent[] events, List<ActiveProfile> profiles) {
        assert (previousTick + 1 == tick);

        boolean noChange = true;
        long[] expectedDeltas = new long[events.length];
        for (ActiveProfile activeProfile : profiles) {
            long scale = activeProfile.invert ? -1 : 1;
            ChangeEvent[] changeEvents = activeProfile.getStage(tick);
            for (int i = 0; i < changeEvents.length; i++) {
                if (changeEvents[i] != null) {
                    expectedDeltas[i] += changeEvents[i].getDelta() * scale;
                    noChange = false;
                }
            }
        }

        if (noChange) {
            return events;
        }

        ChangeEvent[] cleanedEvents = new ChangeEvent[events.length];
        for (int i = 0; i < events.length; i++) {
            ChangeEvent actual = events[i];
            long expectedDelta = expectedDeltas[i];

            if (actual == null) {
                if (expectedDelta != 0) {
                    cleanedEvents[i] = new ChangeEvent(
                            i,
                            -expectedDelta,
                            -expectedDelta,
                            tick
                    );
                }

                continue;
            }

            if (actual.getDelta() != expectedDelta) {
                cleanedEvents[i] = new ChangeEvent(
                        i,
                        actual.getDelta() - expectedDelta,
                        actual.getFinalPower() - expectedDelta,
                        actual.getTick()
                );
            }
        }

        return cleanedEvents;
    }

    private ActiveProfile applyProfile(long currentTick, int profileIdx) {
        ActiveProfile profile = new ActiveProfile(profiles[profileIdx], currentTick, latestStates[profileIdx]);
        activeProfiles.add(profile);
        latestStates[profileIdx] = !latestStates[profileIdx];
        long tick = currentTick - profiles[profileIdx].getEvents().get(0).getTick();
        addSignalEvent(new SignalEvent(profileIdx, latestStates[profileIdx], tick));
        return profile;
    }

    private void addSignalEvent(SignalEvent event) {
        List<SignalEvent> signalEvents = signalUpdates.get(event.getTick());
        if (signalEvents == null) {
            signalEvents = new ArrayList<>();
            signalUpdates.put(event.getTick(), signalEvents);
        }
        System.out.println("Discovered: " + event);
        signalEvents.add(event);
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

        public ChangeEvent[] getStage(long tick) {
            ChangeEvent[] changeEvents = new ChangeEvent[profile.getEvents().size()];
            tick -= startTick;
            for (ChangeEvent event : profile.getEvents()) {
                if (event.getTick() == tick) {
                    changeEvents[event.getReceiver()] = event;
                }
                if (event.getTick() > tick) {
                    return changeEvents;
                }
            }
            return changeEvents;
        }

        public long getSkippedTick(long lastTick, long currentTick) {
            for (ChangeEvent changeEvent : profile.getEvents()) {
                if (changeEvent.getTick() + startTick > lastTick && changeEvent.getTick() + startTick < currentTick) {
                    return changeEvent.getTick() + startTick;
                }
            }
            return -1;
        }

        public boolean isOver(long tick) {
            return profile.getEvents().get(profile.getEvents().size() - 1).getTick() + startTick < tick;
        }
    }
}
