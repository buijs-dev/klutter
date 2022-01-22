#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Annotations modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

cd "klutter-annotations" || cd ".." && cd "klutter-annotations"

echo "\0/ Klutter: step: build annotations-jvm modules"
echo "------------------"
./gradlew clean -p "annotations-jvm"
./gradlew build -p "annotations-jvm"

echo "\0/ Klutter: step: publish annotations-jvm modules"
echo "------------------"
./gradlew publish -p "annotations-jvm"

echo "\0/ Klutter: step: build annotations-kmp modules"
echo "------------------"
./gradlew clean -p "annotations-kmp"
./gradlew build -p "annotations-kmp"

echo "\0/ Klutter: step: publish annotations-kmp modules"
echo "------------------"
./gradlew publish -p "annotations-kmp"

echo "\0/ Klutter: step: build annotations-processor modules"
echo "------------------"
./gradlew clean -p "annotations-processor"
./gradlew build -p "annotations-processor"

echo "\0/ Klutter: step: publish annotations-processor modules"
echo "------------------"
./gradlew publish -p "annotations-processor"

cd ".."