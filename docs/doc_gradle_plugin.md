# Klutter Gradle Plugin

The main purpose of the Klutter Plugin is to connect a Flutter frontend with a Kotlin Multiplatform backend.
It also aims to streamline the production process of building and releasing cross-platform apps.
Combining Flutter and KMP has a few challenges. At very least there is the difference in ecosystems.
Flutter is Dart based and uses the PUB ecosystem. Kotlin Multiplatform is Kotlin based and uses the Gradle ecosystem.
Flutters Android module uses Gradle but not the newer Kotlin based version. It uses the Groovy version, which does not work the
same as Kotlin.

Klutter is Kotlin first, which means it uses Kotlin as much as possible. The Klutter plugin has a set of Gradle tasks
which will generate anything from settings files to Kotlin/Dart/Groovy code needed to make Flutter and KMP work together.
To do this a set of tasks is available:

1. [generateAdapters](doc_gradle_plugin_task_generate_adapter.md)
2. [generateAndroidBuildGradle](doc_gradle_plugin_task_generate_android_build_gradle.md)
3. [generateIOSPodfile](doc_gradle_plugin_task_generate_ios_podfile.md)
4. [updatePlatformPodspec](doc_gradle_plugin_task_update_platform_podspec.md)
5. [setupProject](doc_gradle_plugin_task_setup_project.md)