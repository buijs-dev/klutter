# Klutter Gradle Plugin

The main purpose of the Klutter Plugin is to connect a Flutter frontend with a Kotlin Multiplatform backend.
It also aims to streamline the production process of building and releasing cross-platform apps.
Combining Flutter and KMP has a few challenges. At very least there is the difference in ecosystems.
Flutter is Dart based and uses the PUB ecosystem. Kotlin Multiplatform is Kotlin based and uses the Gradle ecosystem.
Flutters Android module uses Groovy Gradle which is not exactly the same as the newer Kotlin Gradle.

The Klutter plugin has a set of Gradle tasks which will generate anything from settings files to 
Kotlin/Dart/Groovy code needed to make Flutter and KMP work together. It also makes releasing easier
by providing a set of pre-configured Fastlane based tasks (work in progress).

## Tasks
1. [generateAdapters](doc_gradle_plugin_task_generate_adapter.md)
2. [updatePlatformPodspec](doc_gradle_plugin_task_update_platform_podspec.md)
3. [updateProject](doc_gradle_plugin_task_update_project.md)