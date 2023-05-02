#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter compiler modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build compiler modules"
echo "------------------"
./gradlew clean -p "lib/compiler"
./gradlew build -p "lib/compiler"

echo "\0/ Klutter: step: publish compiler modules"
echo "------------------"
./gradlew publish -p "lib/compiler"
./gradlew publishToMavenLocal -p "lib/compiler"