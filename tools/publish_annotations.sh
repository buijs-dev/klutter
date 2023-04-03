#!/bin/sh
#stop script on failure
set -e

cd ".."

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
./gradlew clean -p "lib/annotations"
./gradlew build -p "lib/annotations"

echo "\0/ Klutter: step: publish annotations modules"
echo "------------------"
./gradlew publish -p "lib/annotations"