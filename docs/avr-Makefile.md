# AVR Makefile
Can be modified using some constants:

* `CODE` must be set that `$(CODE).c` matches your source file
* `MCU` is the MCU of the AVR (e. g. `atmega32`)
* `AVRDUDE_SHORT_CODE` is the avrdude term for the MCU (see avrdude's man page)
* `PORT` is a valid avrdude port (e. g. `usb`)
* `PROGRAMMER` a avrdude programmer (see man page)

## Usage
* `make` compiles it
* `make flash` flashes the configured avr
