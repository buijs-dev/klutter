#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter modules >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

./gradlew publishToMavenLocal -p "lib/annotations"
./gradlew publishToMavenLocal -p "lib/bill-of-materials"
./gradlew publishToMavenLocal -p "lib/compiler"
./gradlew publishToMavenLocal -p "lib/gradle"
./gradlew publishToMavenLocal -p "lib/kompose"
./gradlew publishToMavenLocal -p "lib/kore"
./gradlew publishToMavenLocal -p "lib/tasks"