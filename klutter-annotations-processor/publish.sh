#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Annotations Processor modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build annotations-processor modules"
echo "------------------"
./gradlew clean
./gradlew build

echo "\0/ Klutter: step: publish annotations-processor modules"
echo "------------------"
./gradlew publish
