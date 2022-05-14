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
1. [generateAdapters](docs/doc_gradle_plugin_task_generate_adapter.md)
2. [updatePlatformPodspec](docs/doc_gradle_plugin_task_update_platform_podspec.md)
3. [updateProject](docs/doc_gradle_plugin_task_update_project.md)

## License
MIT License

Copyright (c) [2021] [Buijs Software]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.