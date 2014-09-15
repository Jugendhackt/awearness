# awearness
vibrating wristbands are awesome

## Details please!
awearness is a [3d-printed](./design) wristband that vibrates when it is triggered by the app running on the smartphone that is connected via Bluetooth Low Energy.
Inside the wristband is a [RFduino](http://rfduino.com) which listens for certain [commands](./docs/RFduino_wristband/wristband_commands.md).
This is how the vibration motor or a LED is triggered. 

The example application triggers the wristband when a surveillance camera is nearby. The data comes from a
self-written API that gets its data from Open Street Map. (Sadly not yet in this repo).
