#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Locally >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build all modules"
echo "------------------"
./gradlew clean -p "lib"
./gradlew build -p "lib"

echo "\0/ Klutter: step: publish core modules"
echo "------------------"
./gradlew publishToMavenLocal -p "lib"
