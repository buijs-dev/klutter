#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Core modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build core modules"
echo "------------------"
./gradlew clean -p "packages/core"
./gradlew build -p "packages/core"

echo "\0/ Klutter: step: publish core modules"
echo "------------------"
./gradlew publish -p "packages/core"
