#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter Test-KIT module >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build kitty modules"
echo "------------------"
./gradlew clean -p "lib/testkit"
./gradlew build -p "lib/testkit"

echo "\0/ Klutter: step: publish kitty modules"
echo "------------------"
./gradlew publish -p "lib/testkit"
