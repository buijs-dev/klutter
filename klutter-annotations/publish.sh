#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Annotations modules to Repsy >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build annotations modules"
echo "------------------"
./gradlew clean
./gradlew build

echo "\0/ Klutter: step: publish annotations modules"
echo "------------------"
./gradlew publish
