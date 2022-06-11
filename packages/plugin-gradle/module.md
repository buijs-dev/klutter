# Klutter Gradle Plugin

The main purpose of the Klutter Gradle Plugin is to connect a Flutter frontend with a Kotlin Multiplatform backend.
This plugin provides a set of Gradle tasks which will generate anything from settings files to
Kotlin/Dart/Groovy code needed to make Flutter and KMP work together. 

<B>Important:</B> For using and/or creating Flutter plugins with Klutter you should use the pub [plugin](https://github.com/buijs-dev/klutter-dart).

## Installation
Preferred way of installing/using this plugin is by using the pub [plugin](https://github.com/buijs-dev/klutter-dart).

## Tasks
1. [klutterExcludeArchsPlatformPodspec](#Task:%20klutterExcludeArchsPlatformPodspec)
2. [klutterGenerateAdapters](#Task:%20klutterGenerateAdapters)

# Task: klutterExcludeArchsPlatformPodspec
A Flutter app using Kotlin Multiplatform code won't run on an iOS simulator.
There's an easy fix for that: Exclude arm64 architecture from the podspec file.
This task does so by adding the following 2 lines of code to the KMP module .podspec file:

```
 pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }""")
 user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }""")
```

# Task: klutterGenerateAdapters
The generate adapters task creates all the boilerplate code needed to make the Dart code in Flutter
communicate with Kotlin in the Multiplatform module. 

The following steps are executed to do so:
- Scan the KMP module for annotations.
- Convert Kotlin classes to Dart.
- Generate a library Dart file in the root/lib folder.
- Generate an adapter Kotlin file in the root/android folder.
- Generate an adapter Swift file in the root/ios folder.

The generated Dart library then gives access to any native code written in Kotlin Multiplatform.