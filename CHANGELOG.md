# Klutter release notes

## v2022.r6-5.alpha
- Improved generated Flutter code to handle null values.
- Removed AdapterResponse class from generated Flutter code and added klutter-dart import.
- Added AndroidContext annotation to pass Android Context.
- [Bugfix] Fixed adding exclusions to Podfile when they are already added.

## v2022.r6-2.alpha
- [Bugfix] Fixed missing dart:convert import in generated flutter adapter class.

## v2022.r6-1.alpha
- [Bugfix] Fixed standard nullable fields to be incorrectly flagged as custom datatype.

## v2022.r6.alpha
- [Bugfix] klutterExcludeArchsPlatformPodspec: A warning is now logged when adding exclusion fails.
- [Bugfix] klutterGeneratedAdapters: Do not append Dart Adapter class with 'Plugin'.

## v2022-alpha-5
- Removed all support for a Klutter project.
- Removed redundant tasks.
- Removed redundant DSL for Gradle build file.
- Renamed generateAdapters task to klutterGenerateAdapters.
- Add new task klutterExcludeArchsPlatformPodspec.
- Refactored core internals for easier maintenance.

## v2022-alpha-3
- Add support for generating boilerplate code in a Flutter project.
- Separate AndroidManifest deserializer from the AndroidManifestVisitor.
- Platform applicationId is changed to include 'platform'. Having the same applicationId for the platform module 
and app module causes issues when building for Android.
- Renamed klutter.properties to klutter.secrets to better highlight it's use.
- Klutter secrets utility looks in env variables for a variable name if not found in klutter.secrets.
- Add signingConfig for release build in android/app build.gradle

## v2022-pre-alpha-5
- Extract all task functionality from the plugins module and add it to klutter-core. CLI and Gradle plugin can now both use the same functionality.
- Move annotations-processor to klutter-core because there is no requirement to use the processor on any other place.
- Remove gradle-plugins module from klutter repo and to it's own [repo](https://github.com/buijs-dev/klutter-gradle)
- Add buildSrc to the template.
