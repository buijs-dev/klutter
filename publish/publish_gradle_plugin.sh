#!/bin/sh
set -e
./gradlew clean
./gradlew build
./gradlew publish