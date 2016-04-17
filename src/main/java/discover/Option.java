package discover;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author plorio
 */
public class Option {
    private final TreeMap<Long, List<SignalEvent>> signalUpdates;
    private final TransmitterProfile[] profiles;
    private final List<ActiveProfile> activeProfiles;
    private final boolean[] latestStates;

    private final List<Option> tangents;

    public Option(TransmitterProfile[] profiles) {
        this.profiles = profiles;
        signalUpdates = new TreeMap<>();
        activeProfiles = new ArrayList<>();
        latestStates = new boolean[profiles.length];
        tangents = new ArrayList<>();
    }

    private long lastProcessedTick = 0;

    public boolean processEvents(long currentTick, List<ChangeEvent> events) {
        if (currentTick == 430) {
            System.out.println("DEBUG");
        }
        if (tangents.size() > 0) {
            // process all tangents, clean up those that die
            List<Option> deadTangents = new ArrayList<>();
            for (Option tangent : tangents) {
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
                Option reality = tangents.get(0);

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

        // Filter out profiles we've already discovered
        List<ActiveProfile> deadProfiles = new ArrayList<>();
        for (ActiveProfile activeProfile : activeProfiles) {
            if (activeProfile.skippedStage(lastProcessedTick, currentTick)) {
                System.out.println("Skipped expected stage of active profile");
                return false;
            }

            if (activeProfile.isOver(currentTick)) {
                deadProfiles.add(activeProfile);
                continue;
            }

            long scale = activeProfile.invert ? -1 : 1;
            ChangeEvent expected = activeProfile.getStage(currentTick);
            if (expected != null) {
                List<ChangeEvent> cleanedEvents = new ArrayList<>(events.size());
                for (ChangeEvent event : events) {
                    if (event.getReceiver().equals(expected.getReceiver())) {
                        if (event.getDelta() == expected.getDelta() * scale) {
                            continue;
                        }
                        if (!activeProfile.invert && event.getFinalPower() < expected.getDelta()) {
                            System.out.println("Active profile is not possible");
                            return false;
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

        deadProfiles.forEach(activeProfiles::remove);

        for (ChangeEvent event : events) {
            NumberSum<Integer> profileOptions = new NumberSum<>();
            for (int i = 0; i < profiles.length; i++) {
                if (profiles[i].getFirstReceiver().equals(event.getReceiver())) {
                    int scale = latestStates[i] ? -1 : 1;
                    profileOptions.add(scale * profiles[i].getEvents().get(0).getDelta(), i);
                }
            }

            List<List<Integer>> options = profileOptions.options(event.getDelta());
            if (options.size() == 0) {
                System.out.println("No possible way to create this event");
                return false;
            }

            if (options.size() == 1) {
                List<Integer> activatedProfiles = options.get(0);
                for (int idx : activatedProfiles) {
                    applyProfile(currentTick, idx);
                }
            } else {
                for (List<Integer> option : options) {
                    Option tangent = new Option(profiles);
                    tangent.activeProfiles.addAll(activeProfiles);
                    System.arraycopy(latestStates, 0, tangent.latestStates, 0, latestStates.length);
                    tangent.lastProcessedTick = currentTick;

                    for (int idx : option) {
                        applyProfile(currentTick, idx);
                    }

                    tangents.add(tangent);
                }
            }
        }

        lastProcessedTick = currentTick;
        return true;
    }

    private void applyProfile(long currentTick, int profileIdx) {
        latestStates[profileIdx] = !latestStates[profileIdx];
        activeProfiles.add(new ActiveProfile(profiles[profileIdx], currentTick, latestStates[profileIdx]));
        long tick = currentTick - profiles[profileIdx].getEvents().get(0).getTick();
        addSignalEvent(new SignalEvent(profileIdx, latestStates[profileIdx], tick));
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

        public boolean isOver(long tick) {
            return profile.getEvents().get(profile.getEvents().size() - 1).getTick() < tick;
        }
    }
}