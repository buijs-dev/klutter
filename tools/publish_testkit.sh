#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter Test module >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build kitty modules"
echo "------------------"
./gradlew clean -p "lib/klutter-test"
./gradlew build -p "lib/klutter-test"

echo "\0/ Klutter: step: publish klutter-test modules"
echo "------------------"
./gradlew publish -p "lib/klutter-test"
