#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter Kompose modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build kompose modules"
echo "------------------"
./gradlew clean -p "lib/kompose"
./gradlew build -p "lib/kompose"

echo "\0/ Klutter: step: publish kompose modules"
echo "------------------"
./gradlew publish -p "lib/kompose"
