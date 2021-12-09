#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Core modules to Repsy >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build core modules"
echo "------------------"
./gradlew clean -p "adapter"
./gradlew build -p "adapter"

echo "\0/ Klutter: step: publish core modules"
echo "------------------"
./gradlew publish
