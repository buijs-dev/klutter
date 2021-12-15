#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Plugin modules to Repsy >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build plugin modules"
echo "------------------"
./gradlew clean
./gradlew build

echo "\0/ Klutter: step: publish plugin modules"
echo "------------------"
./gradlew publish
