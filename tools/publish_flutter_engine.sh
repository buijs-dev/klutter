#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter Flutter Engine modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build Flutter Engine modules"
echo "------------------"
./gradlew clean -p "lib/flutter-engine"
./gradlew build -p "lib/flutter-engine"

echo "\0/ Klutter: step: publish Flutter Engine modules"
echo "------------------"
./gradlew publish -p "lib/flutter-engine"
