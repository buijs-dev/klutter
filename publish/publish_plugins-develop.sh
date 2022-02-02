#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Plugin modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

cd "klutter-plugins"

echo "\0/ Klutter: step: build plugin modules"
echo "------------------"
./gradlew clean -p "plugins"
./gradlew build -p "plugins"

echo "\0/ Klutter: step: publish plugin modules"
echo "------------------"
./gradlew publish -p "plugins"