# Medium Propagation Multiplexing

In the internet of things we want to have simple inexpensive clients that can communicate on our network.
We developed a many to one communication system that puts all the complexity and processing on the receiver. Our system
allows for simultaneous communication to the receiver by many clients all on the same frequency. We can achieve this by
the receiver having multiple probes across the medium. A prime candidate for this system would be a network of many
sensors constantly sending their readings to a receiver.

We developed this system by first creating a virtual environment where we could construct and test different algorithms.
Initially we seeked our virtual environment to simulate open air radio communication, but to manage our algorithm we
had to make unrealistic assumptions. In our system when two signals on the same frequency interfere they have perfect
constructive interference at all overlapping locations. Because of this unrealistic assumptions we decided to change
our system to model communication on a single cable, similar to an ethernet hub configuration. Now all transmitters
and receiver probes are connected on a one dimensional medium. In one dimension all the transmitting phases can be
in sync so our assumptions are valid. For simplicity we chose that each transmitter communicates in binary and is
either on or off at any time.

We have transmitters and receiver probes at well defined locations on our medium. At our sample rate we know the
detected signal at each probe. From the probes’ detected signals we are trying to determine the state changes of the
transmitters so we can reconstruct their communications. After attempting many different algorithms we came to
something that seems quite simple in hindsight. The algorithm takes into account the propagation delay from each
transmitter to the probes and the signal strength falloff.

The algorithm has two phases, a warm up phase and a decoding phase. During the warm up phase each transmitter takes a
turn changing its state from off to on. By detecting the changes at the receiver probes we are able to build a profile
for each transmitter. From time zero being the time when the transmitter changes its state from off to on we have each
probe’s detected change and the time it occurred. With the profiles for each transmitter we move to the decoding phase.

The decoding phase starts with each transmitter initially off. When a receiver probe detects a change it builds a list
of all possible combinations of profiles that can describe the current state and change. For instance, given that all
transmitters are off and probe 1 detects a 2 unit signal strength increase it would mean that transmitter A turned on
at time 5 and B at time 3. If there is only one possibility then the future events described in the profiles are
subtracted from future signals. By filtering out the signals we expect to hear, we can listen for new events. If
there are multiple possibilities we continue the decoding phase for each possibility independently as if it were
the only possibility. Doing this can create a tree possible events. However given a proper configuration only one
branch in the tree can be valid, therefore when it is not possible to describe the state and events with the profiles
the branch is collapsed.

Not all configurations converge. Depending on the sample rate, and number and location of the transmitters and receiver
probes, it is possible that the number of possibilities grows exponentially. In the following simulation the sample
rate is high enough to on average take 100 samples before a signal from one transmitter reaches its neighbor. This
would be similar to the transmitters being separated by 1 meter and sampled rate of 3 Ghz. The receiver probes are
spaced evenly across the transmitters. The effective data rate for each transmitter under the ideal conditions of
this simulation is 1.5 Gbps.
