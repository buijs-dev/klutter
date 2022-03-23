#!/bin/sh
#stop script on failure
set -e

echo "Removing Pods and Podfile.lock"
if [ -e "Pods" ]; then
    rm -r "Pods"
fi

if [ -e "Podfile.lock" ]; then
    rm -f "Podfile.lock"
fi

echo "Updating pod"
pod install
pod update