# Klutter

Klutter is a Kotlin Multiplatform + Flutter framework

This is an example app build and released with the Klutter Framework.
Klutter is a framework and tool set which uses Flutter to create the frontend
and Kotlin Multiplatform for the backend. Klutter combines industry best practices
for everything from app design to CICD into a single cohesive framework.

## Getting started

Run build_debug.sh in buildSrc directory to setup the project.
Then sync gradle project.

## Usage

#### Start App
BuildScripts module contains gradle tasks for building debug and release version of the app and running the app.
After syncing the gradle project, these tasks are accessible in the Gradle projectview (if using Android Studio or Intellij).
In Gradle projectview go to klutter - buildScripts - Tasks - other. Available tasks are:  <br />
klutterDebug: Builds a debug version for local development.  <br />
klutterRelease: Builds and signs a release version of the app and uploads it to a test track
in App Store and Playstore.  <br />


#### Dependency management
All dependency versions are accessible through the Klutter object. Each .gradle.kts file can access it by:
```Kotlin
val kotlinVersion = Klutter.config.kotlinVersion
```

Versions can be updated by editing the klutter.yaml file in the root folder.
BuildSrc clean step will convert the yaml file to a klutter.properties file.
Each gradle submodule copies the klutter.properties file to get the dependency versions.

If possible it's preferred to use the Klutter object which exposes all properties.
If it is not possible, for instance in Groovy build.gradle files, then the klutter.properties can be used.
All klutter.properties files are generated during the clean phase which ensures all submodules
use the same dependency versions.

## Contributing
Pull requests are welcome. Contact me at info@buijs.dev

## License
[MIT](https://choosealicense.com/licenses/mit/)

## Getting started

#### Setup Flutter SDK
For a new project go to https://docs.flutter.dev/development/tools/sdk/releases?tab=macos
and download the SDK version you want to develop with. Unzip the file and put in a folder,
for instance 'Users/your-user-name/tools/flutter'. Add this full path to your PATH env variable.
On macos you can open a terminal and do:

```
nano ~/.zshrc
```

Then manually add the sdk absolute path (e.g. 'Users/your-user-name/tools/flutter/bin') to
the path variable. Something like this:

```
export PATH=/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/Users/your-user-name/tools/flutter/bin
```

Save and close the file, then run:

```
source ~/.zshrc
flutter --version
```

When the output contains the flutter version you downloaded then you're all set.

Finally add the distribution url and version to the klutter.yaml. This is helpful for when switching
machines/doing a fresh checkout etc to ensure the same flutter SDK is used.

#### Setup JDK (Java)
Code signing with a different JDK version than Flutter uses might result in problems.  It's best to
set your JAVA_HOME to the JDK version Flutter uses. First check which JDK Flutter uses by running:

```
flutter doctor -v
```

The output will contain something like:

```
[✓] Android toolchain - develop for Android devices (Android SDK version 31.0.0)
• Android SDK at /Users/SomeUser/Library/Android/sdk
• Platform android-31, build-tools 31.0.0
• Java binary at: /Users/SomeUser/Library/Application Support/JetBrains/Contents/jre/Contents/Home/bin/java
• Java version OpenJDK Runtime Environment (build 11.0.10+0-b96-7281165)
• All Android licenses accepted.
```

Copy the location at line 'Java binary at: ...' Open a terminal and do:

```
nano ~/.zshrc
```

Add a new line to export the JAVA_HOME env variable. Don't forget to add quotes around the path
if it contains spaces! Also stop the path at Home, don't add bin/java:

```
export JAVA_HOME="/Applications/Android Studio.app/Contents/jre/jdk/Contents/Home"
```

Save and close the file, then run:

```
source ~/.zshrc
echo $JAVA_HOME
```

When the output contains the path pointing to the added export then you're done.
To test it run:

```
flutter clean
flutter build appbundle
```

#### Setup Gradle
First time setup is done by running the build_debug.sh script in /buildScripts submodule.
This will create all the necessary dependencies and files need for Gradle to succesfully
synchronize all custom tasks. Reload Gradle project and then all tasks should be visible.

#### Setup Fastlane CI
Fastlane can deploy to the Playstore after an app has been added manually and a release has been uploaded.
Follow these steps to do so: https://docs.flutter.dev/deployment/android
For reference, use this command to locally build an app bundle: flutter build appbundle --obfuscate --split-debug-info=/<directory>
Next Fastlane can be setup by following the steps described here: https://docs.fastlane.tools/getting-started/android/setup/#setting-up-supply