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

cd ".."
cd "klutter-core"

echo "\0/ Klutter: step: build core modules"
echo "------------------"
./gradlew clean -p "core"
./gradlew build -p "core"

echo "\0/ Klutter: step: publish core modules"
echo "------------------"
./gradlew publish -p "core"
