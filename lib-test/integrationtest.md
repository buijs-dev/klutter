# Integration Testing

The Klutter Framework project consists of klutter (this project) and klutter-dart (pub plugin).
There is a small amount of circular dependencies between the 2 project which should be resolved
when the time is right. In the current phase it is to uncertain what abstraction is the best fit
so for now this will have to do. The following steps verify the framework as a whole and should
all be passed successfully before publishing a new release (based of version 2022.r6-9.alpha).

## Prerequisites
- Both klutter/lib and klutter-dart projects builds successfully without ignored tests (comply or explain if tests are disabled).
- Both klutter and klutter-dart have no uncommitted changes (important because IT requires a few local changes).

## Setup
- Go to GeneratePluginProjectTask.kt in klutter/lib/klutter-tasks. 
Change the following lines: 

```kotlin
updated.add("  klutter: ^$klutterPubVersion")
```

to:
```kotlin
updated.add("  klutter: \n      path: <path to local klutter-dart folder>")
```

Instead of getting the klutter-dart library from PUB, the generated project will use the local library.

- Publish klutter/lib modules to mavenLocal:
```shell
./gradlew clean build publishToMavenLocal
```

Grab a coffee, this will take a while.

- In klutter-dart find all references to the buijs.dev repsy repository and replace it with mavenLocal().

So replace:
```kotlin
maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
```

with:
```kotlin
mavenLocal()
```

- In klutter-dart update variable klutterGradleVersion in klutter-dart/lib/src/common/config.dart to the klutter release version.

## Testcases
The goal of the following testcases is to use all Gradle and Flutter tasks atleast once.

- [Create plugin project from Intellij IDE](#Create%20plugin%20project%20from%20Intellij%20IDE)
- [Create plugin project from Android Studio IDE](#Create%20plugin%20project%20from%20Android%20Studio%20IDE)

### Create plugin project from Intellij IDE 
Start Intellij sandbox:

```shell
./gradlew runIde
```

In welcome screen verify:
- Klutter title 
- Klutter logo
- Correct release version
- Correct description

Go to projects.
Select <B>New Project</B>. Verify:
- Klutter logo
- Klutter menu title
- Type has value <B>plugin</b>

Select klutter option.
Enter:
- name: my_e2e_plugin
- group: dev.buijs.e2e.awesome.plugin

Click on next. Click on finish. Verify a new flutter project is 
created with this name and group which applies the klutter plugin.

Ensure the Gradle project is synced correctly.
Open a terminal in the plugin project and run:

```shell
flutter pub run klutter:producer install=platform
```

Verify build is successful:
- root/ios/Klutter should contain Platform.xcframework
- root/android/klutter should contain .aar file 

Stop runIde task and open the generated project in Android Studio.
In the root/example folder run:

```shell
flutter pub run klutter:consumer add=pluginName
```

Fire up emulator and simulator and verify the app works on both.
Run pod update in example/ios if it does not boot up!

Repeat above steps with default plugin name and group:
- name: my_plugin
- group: com.example

### Create plugin project from Android Studio IDE
// TODO

