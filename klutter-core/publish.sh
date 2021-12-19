#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Core modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build core modules"
echo "------------------"
./gradlew clean
./gradlew build

echo "\0/ Klutter: step: publish core modules"
echo "------------------"
./gradlew publish
