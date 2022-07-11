#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter Gradle Plugin >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build plugin-gradle modules"
echo "------------------"
./gradlew clean -p "lib/plugin-gradle"
./gradlew build -p "lib/plugin-gradle"

echo "\0/ Klutter: step: publish plugin-gradle modules"
echo "------------------"
./gradlew publish -p "lib/plugin-gradle"