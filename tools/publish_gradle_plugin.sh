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

echo "\0/ Klutter: step: build klutter-gradle modules"
echo "------------------"
./gradlew clean -p "lib/klutter-gradle"
./gradlew build -p "lib/klutter-gradle"

echo "\0/ Klutter: step: publish klutter-gradle modules"
echo "------------------"
./gradlew publish -p "lib/klutter-gradle"