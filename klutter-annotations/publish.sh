#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Annotations modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build annotations modules"
echo "------------------"
./gradlew clean -p "lib"
./gradlew build -p "lib"

echo "\0/ Klutter: step: publish annotations modules"
echo "------------------"
./gradlew publish
