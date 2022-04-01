# Klutter
[![GitHub license](https://img.shields.io/github/license/buijs-dev/klutter)](#License)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/dev.buijs.klutter.gradle?color=blueviolet)](https://plugins.gradle.org/plugin/dev.buijs.klutter.gradle)
[![Repsy maven](https://img.shields.io/badge/maven-2022--pre--alpha--5-blue)](https://repsy.io/mvn/buijs-dev/klutter/dev/buijs/klutter/)

Klutter is a framework and tool set which uses Flutter to create the frontend
and Kotlin Multiplatform for the backend. The connective layer is generated
by the Klutter framework. Klutter combines industry best practices
for everything from app design to CICD into a single cohesive framework.

<img src="https://raw.githubusercontent.com/buijs-dev/klutter/main/docs/klutter.png" alt="klutter-overview" width="300"/>

[Source of this picture and a good read](https://littlegnal.github.io/2019-07-09/kmpp_flutter_en)


# Klutter is in PRE-ALPHA stage!
Pre-alpha means the inital POC phase was successful and the concept works but nothing is set in stone yet. 
The next stage, being alpha, is reached once all components are created. I am just one developer working
on this project in my free time. To see what I'm working on check [here](https://github.com/users/buijs-dev/projects/1).
For now don't use this for production or any serious project yet but feel free to ex

<b>Important:</b> Checkout the latest branch (see release notes) for stable releases. 
The main-branch is currently used for development.

- [Release-notes](Release-notes.md)
- [Documentation](https://buijs-dev.github.io/klutter/)
- [Project Structure](docs/doc_project_structure.md)
- [Configuration management](docs/doc_configuration_management.md)

## Klutter Gradle Plugin:
Gradle plugin to manage the Klutter project: [klutter Gradle Plugin](https://github.com/buijs-dev/klutter-gradle)

## Klutter CLI:
Command line to create and manage a Klutter project: [klutter CLI](https://github.com/buijs-dev/klutter-cli)

# Gettings started

Download the CLI tool: https://www.dropbox.com/s/zc1ctg5tdcvu177/klutter-cli-pre-alpha-5.zip

Unzip the file, move to folder cli/bin and run:

```shell
./klutter create
```

This generates a Klutter project in the folder where the CLI tool is unzipped.
For more information see [Klutter CLI](https://buijs-dev.github.io/klutter-cli/)

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
