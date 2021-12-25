#!/bin/sh
#stop script on failure
set -e

echo " ____________
< Publishing Klutter Transpiler Dart module >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build annotations module"
echo "------------------"
./gradlew clean -p "transpiler-dart"
./gradlew build -p "transpiler-dart"

echo "\0/ Klutter: step: publish transpiler-dart module"
echo "------------------"
./gradlew publish -p "transpiler-dart"