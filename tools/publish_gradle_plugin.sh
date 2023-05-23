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

echo "\0/ Klutter: step: build gradle modules"
echo "------------------"
./gradlew clean -p "lib/gradle"
./gradlew build -p "lib/gradle"

./gradlew copyKlutterProperties -p "lib/gradle"

echo "\0/ Klutter: step: publish gradle modules"
echo "------------------"
#./gradlew publish -p "lib/gradle"
./gradlew publishToMavenLocal -p "lib/gradle"