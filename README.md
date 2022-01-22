![This is an image](examples/example-basic/assets/metadata/icon/banner.png)

# Klutter
[![GitHub license](https://img.shields.io/github/license/buijs-dev/klutter)](#License)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/dev.buijs.klutter.gradle?color=blueviolet)](https://plugins.gradle.org/plugin/dev.buijs.klutter.gradle)
[![Repsy maven](https://img.shields.io/badge/maven-2022--pre--alpha--2-blue)](https://repsy.io/mvn/buijs-dev/klutter/dev/buijs/klutter/)

Klutter is a framework and tool set which uses Flutter to create the frontend
and Kotlin Multiplatform for the backend. The connective layer is generated
by the Klutter framework. Klutter combines industry best practices
for everything from app design to CICD into a single cohesive framework.


# Klutter is in PRE-ALPHA stage!
- [Documentation](https://buijs-dev.github.io/klutter/)
- [Project Structure](docs/doc_project_structure.md)
- [Klutter Gradle Plugin](docs/doc_gradle_plugin.md)
- [Configuration management](docs/doc_configuration_management.md)


### Code generation
* [x] Generate android build.gradle in root/android and root/android/app
* [x] Generate PodFile in root/ios and AppDelegate.swift in ios/Runner 
* [x] Generate adapter.dart in root/lib/generated
* [x] Read annotations from KMP source and generate simple method calls
* [x] Generate PodFile in root/ios and AppDelegate.swift in ios/Runner
* [ ] Read annotations from KMP source and generate method calls that take args

### Example apps
* [x] [example-basic](examples/example-basic) Add basic project which works on iOS and Android
* [ ] Add example project which uses libraries in KMP
* [ ] Add example project which uses more advanced method calls
  
### Configuration management
* [x] Read yaml files for configuration from root/klutter folder
* [x] Separate public, local and secret config with yaml files
* [ ] Add encryption support for secrets
* [x] Add repository management 
* [ ] Add mechanism for key file loading/storing

### Continuous Integration and Delivery
* [ ] Add tasks to release new builds to App Store/Playstore

### Kotlin - Dart transpiler
* [ ] Create Klutter UI components in Kotlin
* [ ] Embed Klutter UI components in Flutter views
* [ ] Create Klutter UI library

## Contributing
Pull requests are welcome. Contact me at info@buijs.dev

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
