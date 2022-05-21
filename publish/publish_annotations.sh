#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter Annotations modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build annotations-jvm modules"
echo "------------------"
./gradlew clean -p "packages/annotations-jvm"
./gradlew build -p "packages/annotations-jvm"

echo "\0/ Klutter: step: publish annotations-jvm modules"
echo "------------------"
./gradlew publish -p "packages/annotations-jvm"

echo "\0/ Klutter: step: build annotations-kmp modules"
echo "------------------"
./gradlew clean -p "packages/annotations-kmp"
./gradlew build -p "packages/annotations-kmp"

echo "\0/ Klutter: step: publish annotations-kmp modules"
echo "------------------"
./gradlew publish -p "packages/annotations-kmp"