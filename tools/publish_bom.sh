#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter BOM modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build BOM"
echo "------------------"
./gradlew clean -p "lib"
./gradlew build -p "lib"

echo "\0/ Klutter: step: publish BOM"
echo "------------------"
./gradlew publish -p "lib"