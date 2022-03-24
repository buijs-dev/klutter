![This is an image](examples/assets/metadata/icon/banner.png)

# Klutter
[![GitHub license](https://img.shields.io/github/license/buijs-dev/klutter)](#License)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/dev.buijs.klutter.gradle?color=blueviolet)](https://plugins.gradle.org/plugin/dev.buijs.klutter.gradle)
[![Repsy maven](https://img.shields.io/badge/maven-2022--pre--alpha--4b-blue)](https://repsy.io/mvn/buijs-dev/klutter/dev/buijs/klutter/)

Klutter is a framework and tool set which uses Flutter to create the frontend
and Kotlin Multiplatform for the backend. The connective layer is generated
by the Klutter framework. Klutter combines industry best practices
for everything from app design to CICD into a single cohesive framework.

<img src="https://raw.githubusercontent.com/buijs-dev/klutter/main/docs/klutter.png" alt="klutter-overview" width="300"/>

[Source of this picture and a good read](https://littlegnal.github.io/2019-07-09/kmpp_flutter_en)


# Klutter is in PRE-ALPHA stage!
Pre-alpha you say my good sir? What can I expect then?
It means we have moved away from the inital POC phase and the concept works but anything and everything can 
and/or will change, blowup, refuse to work at any time. If you're in the pre-alpha phase of remodelling your 
house then you might paint a wall green, the next day yellow only to decide to take down the entire wall anyway. 
But then the wife gets pregnant and you need an extra room so you're building up a new wall, but I'll be damned
if it's not going to be a blue wall this time! Anyway... Don't use this for production or any serious project 
yet but feel free to experiment and let me know what you think.

- [Documentation](https://buijs-dev.github.io/klutter/)
- [Project Structure](docs/doc_project_structure.md)
- [Klutter Gradle Plugin](docs/doc_gradle_plugin.md)
- [Configuration management](docs/doc_configuration_management.md)

# Gettings started

Download the CLI tool: https://github.com/buijs-dev/klutter-cli/blob/main/klutter-cli.zip

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
