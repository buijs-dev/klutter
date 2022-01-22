# Klutter Project Structure

A typical project structure is as below. For the simplest example see project [example-basic](example-basic)

```
project
└───android [1]
└───assets [2]
└───ios [3]
└───klutter [4]
└───kmp [5]
└───lib [6]
│   build.gradle.kts [7]
│   settings.gradle.kts [8] 
│   pubspec.yaml [9] 
```

1. Android module (flutter).
2. Shared assets module containing both iOS and Android app + metadata resources including images, i18n/i10n etc.
3. IOS module (flutter).
4. Klutter module containing build.gradle.kts with configured Klutter plugin(s) and .yaml files for app + project configuration.
5. KMP module containing the Kotlin Multiplatform backend.
6. Lib module containing the app frontend (flutter).
7. Project build.gradle.kts which adds the Klutter plugins to the classpath.
8. Project settings.gradle.kts to include Kotlin module: Klutter, KMP and Android
9. Pubspec.yaml (flutter)