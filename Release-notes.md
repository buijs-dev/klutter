# Klutter CLI release notes

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
