#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter bill-of-materials modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build bill-of-materials modules"
echo "------------------"
./gradlew clean -p "lib/bill-of-materials"
./gradlew build -p "lib/bill-of-materials"

echo "\0/ Klutter: step: publish bill-of-materials modules"
echo "------------------"
#./gradlew publish -p "lib/bill-of-materials"
./gradlew publishToMavenLocal -p "lib/bill-of-materials"