# Klutter

Klutter is a framework and tool set which uses Flutter to create the frontend
and Kotlin Multiplatform for the backend. The connective layer is generated
by the Klutter framework. Klutter combines industry best practices
for everything from app design to CICD into a single cohesive framework.

## Project structure

A typical project structure is as below. For the simplest example see [project example-basic](example-basic)

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

(1) Android module (flutter).
(2) Shared assets module containing both iOS and Android app + metadata resources including images, i18n/i10n etc.
(3) IOS module (flutter).
(4) Klutter module containing build.gradle.kts with configured Klutter plugin(s) and .yaml files for app + project configuration.
(5) KMP module containing the Kotlin Multiplatform backend.
(6) Lib module containing the app frontend (flutter).
(7) Project build.gradle.kts which adds the Klutter plugins to the classpath.
(8) Project settings.gradle.kts to include Kotlin module: Klutter, KMP and Android
(9) Pubspec.yaml (flutter)

## Plugins

### Klutter - Adapter

### Klutter - Config
The config plugin is used to configure the project.
A fully configured plugin will do the following tasks:
<ol>
  <li>Read the klutter.yaml file and write all properties to the root build.gradle.kts</li>
  <li>Create a build.gradle.kts template</li>
  <li>Merge all build.gradle.kts file with the template</li>
</ol>

A Klutter project should not be a multicomponent gradle project.
Instead all modules should be standalone.This means at minimum the
Flutter module (frontend) and the KMP module (backend). A typical 
Klutter project looks like:




## Contributing
Pull requests are welcome. Contact me at info@buijs.dev

## License
MIT License

Copyright (c) [2021] [Gillian Buijs]

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