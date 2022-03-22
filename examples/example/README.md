# Klutter Example Project
A new klutter application.

# Getting Started
This is the starting point for a Klutter project.

Before the application can be run, the platform module must be build.
On the commandline from the root folder run:

```shell

./gradlew installPlatform

```

This task will create an .aar file for Android, a podspec for iOS and run a flutter pub get to get all the
Flutter dependencies.

Now you can run the application on an Android emulator or iOS simulator.

For more information on Klutter see: [klutter](https://github.com/buijs-dev/klutter)