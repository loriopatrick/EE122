package discover;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author plorio
 */
public class OptionDecoder {
    private static int options = 0;

    private final int depth;
    private final TreeMap<Long, List<SignalEvent>> signalUpdates;
    private final TransmitterProfile[] profiles;
    private final List<ExpectedChanges> expectedChangesList;
    private final boolean[] latestStates;

    private final List<OptionDecoder> tangents;

    private final long longestPropagation;

    public OptionDecoder(TransmitterProfile[] profiles) {
        this(profiles, 0);
    }

    public OptionDecoder(TransmitterProfile[] profiles, int depth) {
        options += 1;

        this.depth = depth;
        this.profiles = profiles;
        signalUpdates = new TreeMap<>();
        expectedChangesList = new ArrayList<>();
        latestStates = new boolean[profiles.length];
        tangents = new ArrayList<>();

        long maxTick = 0;
        for (TransmitterProfile profile : profiles) {
            long tick = profile.getEvents().get(0).getTick();
            if (tick > maxTick) {
                maxTick = tick;
            }
        }
        longestPropagation = maxTick;
    }


    public int getActiveProfileCount() {
        int s = expectedChangesList.size();
        for (OptionDecoder tangent : tangents) {
            s += tangent.getActiveProfileCount();
        }
        return s;
    }

    public long getLastDecodedTick() {
        return lastProcessedTick - longestPropagation;
    }

    public List<SignalEvent> takeEvents() {
        List<SignalEvent> events = new ArrayList<>();
        List<Long> ticks = new ArrayList<>();
        for (long tick : signalUpdates.navigableKeySet()) {
            if (tick + longestPropagation >= lastProcessedTick) {
                break;
            }
            events.addAll(signalUpdates.get(tick));
            ticks.add(tick);
        }
        ticks.forEach(signalUpdates::remove);
        return events;
    }

    public long getDecoderLag() {
        return longestPropagation;
    }

    private long lastProcessedTick = 0;

    public boolean processChanges(long currentTick, ReceiverChange[] changes) {
        assert (lastProcessedTick + 1 == currentTick);

        if (tangents.size() > 0) {
            // process all tangents, clean up those that die
            List<OptionDecoder> deadTangents = new ArrayList<>();
            for (OptionDecoder tangent : tangents) {
                if (tangent.processChanges(currentTick, changes)) {
                    deadTangents.add(tangent);
                    options -= 1;
                }
            }
            deadTangents.forEach(tangents::remove);

            // all future possibilities are not possible
            // so this must not be possible
            if (tangents.size() == 0) {
                return true;
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
                expectedChangesList.clear();
                expectedChangesList.addAll(reality.expectedChangesList);

                // merge current state
                System.arraycopy(reality.latestStates, 0, latestStates, 0, latestStates.length);
                lastProcessedTick = reality.lastProcessedTick;

                // I am the captain now
                tangents.clear();
                tangents.addAll(reality.tangents);
                options -= 1;
            }

            return false;
        }

        // Filter out events from profiles we've already discovered
        changes = ComputeLayeredChanges(currentTick, changes, expectedChangesList);

        expectedChangesList.removeIf((p) -> p.isOver(currentTick));
        lastProcessedTick = currentTick;

        return !computeAndApplyProfiles(currentTick, changes);
    }

    private boolean computeAndApplyProfiles(long tick, ReceiverChange[] events) {
        for (int i = 0; i < events.length; i++) {
            ReceiverChange event = events[i];

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
                return false;
            }

            // Apply the only possible combination
            if (options.size() == 1) {
                List<Integer> profileIndices = options.get(0);

                List<ExpectedChanges> addedProfiles = profileIndices.stream()
                        .map(idx -> applyProfile(tick, idx)).collect(Collectors.toList());

                // clean events with the our new understanding of the system
                events = ComputeLayeredChanges(tick, events, addedProfiles);
            }
            // Split off into tangents for all combination possibilities
            else {
                for (List<Integer> activatedProfiles : options) {
                    OptionDecoder tangent = new OptionDecoder(profiles, depth + 1);
                    tangent.expectedChangesList.addAll(expectedChangesList);
                    System.arraycopy(latestStates, 0, tangent.latestStates, 0, latestStates.length);
                    tangent.lastProcessedTick = tick;

                    List<ExpectedChanges> addedProfiles = activatedProfiles.stream()
                            .map(idx -> tangent.applyProfile(tick, idx)).collect(Collectors.toList());
                    ReceiverChange[] tangentEvents = ComputeLayeredChanges(tick, events, addedProfiles);
                    tangent.computeAndApplyProfiles(tick, tangentEvents);
                    tangents.add(tangent);
                }
            }
        }

        return true;
    }

    private static ReceiverChange[] ComputeLayeredChanges(long tick, ReceiverChange[] changes, List<ExpectedChanges> profiles) {
        // From active profiles compute the deltas that are expected if there were no changes
        long[] expectedDeltas = new long[changes.length];
        boolean noChange = true;
        for (ExpectedChanges expectedChanges : profiles) {
            long scale = expectedChanges.invert ? -1 : 1;
            ReceiverChange[] receiverChanges = expectedChanges.getStage(tick);
            for (int i = 0; i < receiverChanges.length; i++) {
                if (receiverChanges[i] != null) {
                    expectedDeltas[i] += receiverChanges[i].getDelta() * scale;
                    noChange = false;
                }
            }
        }

        // If no active profiles effect this tick, return
        if (noChange) {
            return changes;
        }

        // Compute and return the changes that were not expected
        ReceiverChange[] layeredChanges = new ReceiverChange[changes.length];
        for (int i = 0; i < changes.length; i++) {
            ReceiverChange actual = changes[i];
            long expectedDelta = expectedDeltas[i];

            if (actual == null) {
                if (expectedDelta != 0) {
                    layeredChanges[i] = new ReceiverChange(
                            i,
                            -expectedDelta,
                            -expectedDelta,
                            tick
                    );
                }

                continue;
            }

            if (actual.getDelta() != expectedDelta) {
                layeredChanges[i] = new ReceiverChange(
                        i,
                        actual.getDelta() - expectedDelta,
                        actual.getFinalPower() - expectedDelta,
                        actual.getTick()
                );
            }
        }

        return layeredChanges;
    }

    private ExpectedChanges applyProfile(long currentTick, int profileIdx) {
        ExpectedChanges profile = new ExpectedChanges(profiles[profileIdx], currentTick, latestStates[profileIdx]);
        expectedChangesList.add(profile);
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
        signalEvents.add(event);
    }

    private static class ExpectedChanges {
        final TransmitterProfile profile;
        final long startTick;
        final boolean invert;

        public ExpectedChanges(TransmitterProfile profile, long discoveryTick, boolean invert) {
            this.profile = profile;
            this.invert = invert;
            startTick = discoveryTick - profile.getEvents().get(0).getTick();
        }

        public ReceiverChange[] getStage(long tick) {
            ReceiverChange[] receiverChanges = new ReceiverChange[profile.getEvents().size()];
            tick -= startTick;
            for (ReceiverChange event : profile.getEvents()) {
                if (event.getTick() == tick) {
                    receiverChanges[event.getReceiver()] = event;
                }
                if (event.getTick() > tick) {
                    return receiverChanges;
                }
            }
            return receiverChanges;
        }

        public boolean isOver(long tick) {
            return profile.getEvents().get(profile.getEvents().size() - 1).getTick() + startTick < tick;
        }
    }

    public static int GetActiveOptions() {
        return options;
    }

    public static void ResetActiveOptions() {
        options = 0;
    }
}
