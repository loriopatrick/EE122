# Notes

This system will either converge or grow exponentially with possibilities. The parameters that can be tuned are
 - the position and number of transmitters and receivers
 - the sampling frequency
 - the signal strength drop off

The simulation was originally setup to model wireless communication. To simulate wired communication we simply restrict
ourselves to placing all network elements in a line. It would be nice to rework the simulation to accurately model
wiring so we could test more complex configurations (graphs).

To get adequate performance we had to model the signal having different strengths based on distance from the transmitter.
With plain copper wire the resistance is too low for this to be accurate. It might be possible to add resistivity to
achieve the effect we modeled.

It might be possible to modify the algorithm to work for radio communication. If we know the phase that each transmitter
is sending with we should be able to build a profile for each transmitter. With the phases known we can calculate how
interference works between the different transmitters at the receiver probes.

Noise is something we did not worry about for this project. The trouble is noise could make our system believe a
profile has become active, when the profile is not confirmed we would have an error. We could break off into another
option in the possibility tree but that would make the system more likely to diverge. A system where the probability of
each possibility might help. The problem might also be fixed by an expected coding scheme by each transmitter. With
the coding scheme the invalid possibilities could be closed faster preventing divergence.

#### Observations

The system grows in the number of possibilities when the receivers hear changes for multiple transmitters during a
single sample. Because of this fact, increasing the sampling frequency and/or spacing the transmitters further apart
will allow for more transmitters per a receiver probe.

When the signal has no drop off (energy loss by distance) the system performs poorly.


#### Performance

The numbers below are simulations with a configuration where transmitters evenly spaced 10 + [0,0.76] meters apart
and probes are placed evenly and symmetrically around the center transmitter.

Format:
    Try <# of transmitters> transmitter : <# of receiver probes> receivers
    <# of possibilities, sampled on change or every 10,000 ticks> <converges?>

```
Try 9 transmitter : 2 receivers 1,1, SUCCESS
Try 10 transmitter : 2 receivers 1,1, SUCCESS
Try 11 transmitter : 2 receivers 1,1, SUCCESS
Try 12 transmitter : 2 receivers 1,1, SUCCESS
Try 13 transmitter : 2 receivers 1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,16383, FAIL
Try 13 transmitter : 3 receivers 1,1, SUCCESS
Try 14 transmitter : 3 receivers 1,1, SUCCESS
Try 15 transmitter : 3 receivers 1,1, SUCCESS
Try 16 transmitter : 3 receivers 1,1, SUCCESS
Try 17 transmitter : 3 receivers 1,1, SUCCESS
Try 18 transmitter : 3 receivers 1,1, SUCCESS
Try 19 transmitter : 3 receivers 1,3,1,3,1,3,1,3,7,15,7,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,7,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,7,3,1,3,1,3,7,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,1,3,7,3,1,3,1,3,7,3,5,3,5,3,5,3,5,3,5,9,5,3,5,3,5,9,5,3,3, FAIL
Try 19 transmitter : 4 receivers 1,1, SUCCESS
Try 20 transmitter : 4 receivers 1,1, SUCCESS
Try 21 transmitter : 4 receivers 1,1, SUCCESS
Try 22 transmitter : 4 receivers 1,1, SUCCESS
Try 23 transmitter : 4 receivers 1,1, SUCCESS
Try 24 transmitter : 4 receivers 1,1, SUCCESS
Try 25 transmitter : 4 receivers 1,1, SUCCESS
Try 26 transmitter : 4 receivers 1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,16383, FAIL
Try 26 transmitter : 5 receivers 1,1, SUCCESS
Try 27 transmitter : 5 receivers 1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,16383, FAIL
Try 27 transmitter : 6 receivers 1,1, SUCCESS
Try 28 transmitter : 6 receivers 1,1, SUCCESS
Try 29 transmitter : 6 receivers 1,1, SUCCESS
Try 30 transmitter : 6 receivers 1,1, SUCCESS
Try 31 transmitter : 6 receivers 1,1, SUCCESS
Try 32 transmitter : 6 receivers 1,1, SUCCESS
Try 33 transmitter : 6 receivers 1,1, SUCCESS
Try 34 transmitter : 6 receivers 1,1, SUCCESS
Try 35 transmitter : 6 receivers 1,1, SUCCESS
Try 36 transmitter : 6 receivers 1,1, SUCCESS
Try 37 transmitter : 6 receivers 1,1, SUCCESS
Try 38 transmitter : 6 receivers 1,3,7,3,7,3,1,3,7,15,7,15,7,15,31,15,7,3,1,3,7,3,1,3,7,15,7,15,7,3,1,3,7,15,31,15,7,15,7,3,7,15,7,15,31,63,31,15,31,15,7,3,3, FAIL
Try 38 transmitter : 7 receivers 1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,16383, FAIL
Try 38 transmitter : 8 receivers 1,1, SUCCESS
Try 39 transmitter : 8 receivers 1,1, SUCCESS
Try 40 transmitter : 8 receivers 1,1, SUCCESS
Try 41 transmitter : 8 receivers 1,1, SUCCESS
Try 42 transmitter : 8 receivers 1,1, SUCCESS
Try 43 transmitter : 8 receivers 1,1, SUCCESS
Try 44 transmitter : 8 receivers 1,1, SUCCESS
Try 45 transmitter : 8 receivers 1,3,7,3,7,3,7,3,7,3,1,3,1,3,7,3,1,3,7,3,1,3,1,3,7,3,7,3,7,3,7,3,1,3,1,3,7,3,7,3,1,3,7,3,1,3,1,3,7,15,31,15,7,3,1,3,7,15,7,3,7,3,1,3,7,3,7,3,1,3,7,3,1,3,7,3,7,3,7,3,1,3,7,3,1,3,7,3,7,3,7,3,7,3,7,3,7,3,7,15,7,3,1,3,1,3,7,3,1,3,7,15,7,15,7,3,1,3,1,3,1,3,1,3,7,3,1,3,7,3,1,3,7,3,1,3,7,3,1,3,1,3,1,3,7,3,7,15,7,15,31,15,31,15,7,15,7,15,7,3,1,3,1,3,1,3,7,3,1,3,1,3,7,3,1,3,1,3,1,3,1,3,7,3,1,3,1,3,7,15,7,3,1,3,7,3,1,3,1,3,7,3,7,3,1,3,1,3,1,3,7,3,1,3,1,3,1,3,7,15,31,63,31,15,7,3,1,3,1,3,7,3,1,3,7,3,7,3,1,3,1,3,7,3,1,3,7,3,1,3,1,3,7,3,1,3,1,3,7,15,7,15,7,3,7,3,7,3,1,3,1,3,7,3,1,3,1,3,7,3,7,3,1,3,1,3,1,3,1,3,1,3,7,3,7,15,7,15,7,3,1,3,7,3,1,3,1,3,1,3,7,15,7,3,1,3,1,3,7,3,1,3,7,3,1,3,7,3,1,3,1,3,1,3,1,3,1,3,7,3,1,3,1,3,7,3,1,3,7,3,1,3,7,15,31,15,7,3,1,3,1,3,7,3,7,15,7,3,7,15,7,3,1,3,1,3,7,15,7,3,1,3,7,15,7,3,1,3,7,15,7,3,1,3,7,15,7,3,1,3,7,3,1,3,7,15,7,15,7,3,1,3,7,3,1,3,1,3,7,3,1,3,1,3,7,3,7,3,1,3,1,3,7,3,1,3,1,3,7,15,7,15,7,3,1,3,7,3,1,3,1,3,7,3,7,3,1,3,7,15,7,3,1,3,7,15,7,15,7,3,1,3,1,3,7,3,7,3,7,15,7,3,1,3,1,3,7,3,1,3,1,3,7,3,1,3,7,3,7,3,1,3,7,3,1,3,1,3,1,3,7,3,1,3,7,3,1,3,1,3,1,3,1,3,7,3,1,3,3,1,3,7,3,7,3,1, SUCCESS
Try 46 transmitter : 8 receivers 1,1, SUCCESS
Try 47 transmitter : 8 receivers 1,1, SUCCESS
Try 48 transmitter : 8 receivers 1,1, SUCCESS
Try 49 transmitter : 8 receivers 1,1, SUCCESS
Try 50 transmitter : 8 receivers 1,1, SUCCESS
Try 51 transmitter : 8 receivers 1,3,1,3,7,3,1,3,7,15,31,63,127,255,511,127,63,31,15,31,63,31,15,7,3,7,3,7,7, FAIL
Try 51 transmitter : 9 receivers 1,3,1,3,1,3,7,3,1,3,7,3,7,3,1,3,1,3,7,3,1,3,1,3,7,3,1,3,7,3,7,3,7,15,7,15,7,3,1,3,1,3,1,3,7,3,1,3,7,3,1,3,1,3,7,15,31,15,7,3,1,3,7,15,7,3,1,3,7,3,1,3,1,3,7,3,7,15,7,15,7,3,1,3,1,3,7,3,1,3,7,3,1,3,1,3,1,3,1,3,7,3,1,3,1,3,1,3,1,3,7,3,1,3,1,3,7,3,1,3,1,3,7,3,7,3,1,3,7,15,7,15,7,3,7,3,1,3,7,3,1,3,7,15,7,3,7,3,1,3,7,3,1,3,1,3,1,3,7,15,7,3,1,3,7,3,7,15,31,15,7,3,1,3,1,3,1,3,7,3,7,3,7,3,7,3,1,3,7,3,1,3,1,3,1,3,7,3,1,3,7,3,1,3,1,3,7,3,7,15,7,15,7,3,7,3,7,3,1,3,1,3,7,15,7,3,1,3,7,15,7,3,7,3,7,15,7,3,1,3,1,3,1,3,7,15,7,3,1,3,1,3,1,3,1,3,7,3,1,3,7,15,7,3,1,3,7,3,1,3,7,3,1,3,1,3,7,15,7,3,1,3,7,3,1,3,1,3,1,3,1,3,7,3,1,3,7,3,1,3,1,3,1,3,7,3,1,3,1,3,1,3,1,3,7,15,7,3,1,3,7,15,7,3,1,3,7,3,1,3,1,3,1,3,7,3,1,3,7,3,1,3,1,3,1,3,7,3,1,3,1,3,1,3,7,15,31,15,31,15,7,3,1,3,1,3,1,3,7,15,7,3,1,3,7,3,7,3,1,3,7,3,1,3,7,15,7,15,7,3,1,3,1,3,7,15,7,15,7,3,1,3,7,3,7,3,1,3,1,3,7,3,1,3,1,3,7,15,7,3,1,3,1,3,7,3,7,3,1,3,7,3,1,3,1,3,1,3,7,3,7,3,1,3,7,3,1,3,1,3,7,3,7,3,7,3,1,3,1,3,7,15,7,3,1,3,1,3,7,3,1,3,7,3,1,3,1,3,7,3,7,3,1,3,1,3,1,3,7,3,1,3,7,3,1,1,3,1,3,1,3,1,3,7,15,31,15,7,3,1,3,7,15,7,3,7,15,31,15,31,15,7,15,31,15,31,63,31,15,7,3, FAIL
Try 51 transmitter : 10 receivers 1,1, SUCCESS
Try 52 transmitter : 10 receivers 1,1, SUCCESS
Try 53 transmitter : 10 receivers 1,1, SUCCESS
Try 54 transmitter : 10 receivers 1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,16383, FAIL
Try 54 transmitter : 11 receivers 1,1, SUCCESS
Try 55 transmitter : 11 receivers 1,1, SUCCESS
Try 56 transmitter : 11 receivers 1,1, SUCCESS
Try 57 transmitter : 11 receivers 1,1, SUCCESS
Try 58 transmitter : 11 receivers 1,1, SUCCESS
```