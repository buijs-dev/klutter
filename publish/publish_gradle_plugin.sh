#!/bin/sh
set -e
cd ".."
./gradlew clean
./gradlew build
./gradlew publish