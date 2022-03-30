# Klutter Project Structure

A typical project structure is as below. For the simplest example see project [example-basic](example-basic)

```
project
└───android [1]
└───assets [2]
└───buildSrc [3]
└───gradle [4]
└───ios [5]
└───lib [6]
└───platform [7]
│   build.gradle.kts [8]
│   settings.gradle.kts [9] 
│   klutter.gradle [10] 
│   klutter.properties [11] 
│   pubspec.yaml [12] 
```

1. Android module (flutter).
2. Shared assets module containing both iOS and Android app + metadata resources including images, i18n/i10n etc.
3. BuildSrc module containing all libraries, versioning etc needed for the project.
4. Gradle wrapper containing the distribution for the project.
5. IOS module (flutter).
6. Lib module containing the app frontend (flutter).
7. Platform (Kotlin Multiplatform) module containing the Kotlin Multiplatform backend.
8. Project build.gradle.kts which adds the Klutter plugins to the classpath.
9. Project settings.gradle.kts to include Kotlin module: Platform, Android and iOS (buildSrc is automatically included)
10. Gradle file containing public project properties.   
11. Properties file containing private project properties.
12. Pubspec.yaml (flutter)