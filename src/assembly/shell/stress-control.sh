#!/bin/sh
modprobe bcm2835-v4l2
java -Djava.library.path=lib -jar stress-control.jar