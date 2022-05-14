# Klutter
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/dev.buijs.klutter.gradle?color=blueviolet&style=for-the-badge)](https://plugins.gradle.org/plugin/dev.buijs.klutter.gradle)
[![Repsy maven](https://img.shields.io/badge/maven-2022--pre--alpha--5-blue?style=for-the-badge)](https://repsy.io/mvn/buijs-dev/klutter/dev/buijs/klutter/)
[![GitHub license](https://img.shields.io/github/license/buijs-dev/klutter?style=for-the-badge)](#License)
[![SONAR](https://img.shields.io/sonar/alert_status/buijs-dev_klutter?label=SONAR&server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/summary/overall?id=buijs-dev_klutter)
[![Codecov](https://img.shields.io/codecov/c/github/buijs-dev/klutter?style=for-the-badge)](https://app.codecov.io/gh/buijs-dev/klutter)

Klutter is a framework and tool set which uses Flutter to create the frontend
and Kotlin Multiplatform for the backend. The connective layer is generated
by the Klutter framework.

<img src="https://raw.githubusercontent.com/buijs-dev/klutter/main/docs/klutter.png" alt="klutter-overview" width="300"/>

[Source of this picture and a good read](https://littlegnal.github.io/2019-07-09/kmpp_flutter_en)


# Klutter is moving to Alpha soon.
Pre-alpha means the inital POC phase was successful and the concept works but nothing is set in stone yet. 
The next stage, being alpha, is reached once all components are created. And that's soon! I'm finishing
up the KMP template and will make it available as the first Alpha release soon. With this you will be able
to create a shared KMP module and publish it as a Flutter package to pub with a single Gradle task.

<b>Important:</b> Checkout the latest branch (see release notes) for stable releases.
The main-branch is currently used for development.

## What's next?
Initially I created Klutter as a monolithic framework to control all parts of the Flutter development process.
Having actually build 2 apps using it I feel it would be better to separate any publishing and/or CI/CD 
functionality to separate modules/components. That's why I started working on 'Delivery', a publishing 
library made in Dart which can build and publish different flavours of your app to App Store, Play Store, Firebase, etc.
Delivery is not yet public, but you can check out the [App Store Connect library](https://github.com/buijs-dev/app-store-connect-dart) 
I'm building which is one it's major components.

- [Release-notes](Release-notes.md)
- [Documentation](https://buijs-dev.github.io/klutter/)
- [Project Structure](docs/doc_project_structure.md)
- [Configuration management](docs/doc_configuration_management.md)


## Klutter Gradle Plugin:
Gradle plugin to manage the Klutter project: [klutter Gradle Plugin](https://github.com/buijs-dev/klutter-gradle)

## Klutter CLI:
Command line to create and manage a Klutter project: [klutter CLI](https://github.com/buijs-dev/klutter-cli)

# Gettings started

[Download](https://www.dropbox.com/s/zc1ctg5tdcvu177/klutter-cli-pre-alpha-5.zip) the tool.

Unzip the file. Move to folder cli/bin. Run:

```shell
./klutter create
```

This generates a Klutter project in the folder where the CLI tool is unzipped.

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
