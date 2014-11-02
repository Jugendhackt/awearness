# awearness moved to [its own place](https://github.com/awearness)
vibrating wristbands are awesome

## Details please!
awearness is an ultra expandable notification concept whose advantages lie in its simplicity and versatility.

We're aiming at developing a wristband that reminds you of things you want to be reminded of. Our first prototyping lead to a [3D-Printed](./design) casing with basic electronics in it. Those electronics include an [RFduino](http://rfduino.com), connecting to any device you wish and waiting for [commands](./docs/RFduino_wristband/wristband_commands.md) to execute.

The first application we developed signals to the wristband when there is a surveillance camera nearby which then vibrates.
We extracted the needed date from Open Street Map, using a [self-written API](./api) Of course it could be sent a signal whenever you like, for example when reaching an accident hotspot while you are going by car or bike. Maybe you want to be reminded of the next place worth seeing, or whenever there is a book store in sight. You decide. 

