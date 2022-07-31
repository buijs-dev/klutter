#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter Kore modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build kore modules"
echo "------------------"
./gradlew clean -p "lib/klutter-kore"
./gradlew build -p "lib/klutter-kore"

echo "\0/ Klutter: step: publish core modules"
echo "------------------"
./gradlew publish -p "lib/klutter-kore"
