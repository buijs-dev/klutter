![This is an image](example-basic/assets/metadata/icon/banner.png)

# Klutter
[![GitHub license](https://img.shields.io/github/license/buijs-dev/klutter)](#License)

Klutter is a framework and tool set which uses Flutter to create the frontend
and Kotlin Multiplatform for the backend. The connective layer is generated
by the Klutter framework. Klutter combines industry best practices
for everything from app design to CICD into a single cohesive framework.

# Klutter is in PRE-ALPHA stage!

## Project structure

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

## Klutter Plugin

The main purpose of the Klutter Plugin is to connect a Flutter frontend with a Kotlin Multiplatform backend.
It also aims to streamline the production process of building and releasing cross-platform apps.
Combining Flutter and KMP has a few challenges. At very least there is the difference in ecosystems.
Flutter is Dart based and uses the PUB ecosystem. Kotlin Multiplatform is Kotlin based and uses the Gradle ecosystem.
Flutters Android module uses Gradle but not the newer Kotlin based version. It uses the Groovy version, which does not work the
same as Kotlin. 

Klutter is Kotlin first, which means it uses Kotlin as much as possible. The Klutter plugin has a set of Gradle tasks
which will generate anything from settings files to Kotlin/Dart/Groovy code needed to make Flutter and KMP work together.
To do this a set of tasks is available:
  - synchronize
  - generateAdapter
  - generateAndroidBuildGradle
  - generateApi
  - buildDebug

### Task: synchronize
The klutter module is the single source of truth of the project. Dependency versions, repositories, etc. 
are all configured in the klutter module using yaml files.  The synchronize task takes all these yaml 
files in the klutter directory, merges them and creates 2 files:
- klutter.gradle.kts
- klutter.properties

These files are created in the klutter/.klutter directory. Any existing klutter.gradle.kts or  
klutter.properties file in this directory will be overwritten. Do <b>not</b> manually edit these files!  
Next the plugin will find all modules configured by the Klutter Plugin. A .klutter directory 
with klutter.gradle.kts and klutter.properties will be created in each module.

More info: [Configuration management in Klutter](.documentation/doc_configuration_management.md)

#### Setup modules
Example of a modules block:

```kotlin
    modules {
        module("kmp")
        module("kmp/common")
        module("android")
        module("android/app")
        module("klutter")
    }
```

By default each module will be resolved by Gradle rootProject directory e.g. if 
the klutter plugin is configured in build.gradle.kts file in directory 
"C:/Users/Anakin/MyProject/fancyapp/klutter" then module("kmp") 
will be resolved to the absolute path "C:/Users/Anakin/MyProject/fancyapp/kmp".

This behavior can be overwritten for either the entire modules block or separately 
per module. Set the root of the project by using the root function. This will result 
in an absolute path e.g. module("kmp") will be resolved to the absolute path "foo/bar/kmp".

```kotlin
    modules {
        root("foo/bar")
        module("kmp")
    }
```

Specifying absolute paths is however most of the times not the way to go. It can cause
issues when absolute paths are checked into version control. What works on your machine
won't work on someone elses if the repository is checked out in a different directory.
Therefore it is recommended to use a relative path by setting the 'absolute' parameter 
to 'false'.  

```kotlin
    modules {
        root("../../foo/bar", absolute = false)
        module("kmp")
    }
```

If the klutter plugin is configured in build.gradle.kts file in directory
"C:/Users/Anakin/MyProject/fancyapp/klutter" then setting the root this way will
resolve module("kmp") to the absolute path "C:/Users/Anakin/foo/bar/kmp".


#### Add configuration
The klutter module is the single source of truth of the project.   Dependency versions, repositories, etc. are all configured in the klutter module using yaml files. Why

This can be setup by adding and editting klutter.yaml files. There are 3 types of yaml files:
- klutter.yaml
- klutter-local.yaml
- klutter-secrets.yaml

##### klutter.yaml
Mandatory file which contains the global project setup, dependency versions etc.

##### klutter-local.yaml
Optional (but recommended) file to store user-dependent configuration. For example
the (absolute) location of SDK's. Should not be saved in version controle.

##### klutter-secrets.yaml
Optional (but recommended) file to store sensitive information. For example credentials, keystore (location), etc.



#### Use configuration
Because the synchronize task adds the generated configuration files to each module,
using it is as easy as applying the klutter.gradle.kts file:

```kotlin
apply(from = ".klutter/klutter.gradle.kts")
```

An idiomatic Kotlin way of applying and using the generated klutter.gradle.kts file is 
by using the .also function:

```kotlin
buildscript {
    apply(from = ".klutter/klutter.gradle.kts").also {
        repositories {
            maven {
                url = uri(project.extra["privateRepoUrl"] as String)
                credentials {
                    username = project.extra["privateRepoUsername"] as String
                    password = project.extra["privateRepoPassword"] as String
                }
            }
        }
    }
}
```

When it's not possible to apply the klutter.gradle.kts file then the klutter.properties
file can be read directly. The synchronization task will keep both files in sync so they
will always contain the same properties. As an example the gradle files in the android 
directory are generated by Klutter and use the klutter.properties file instead of the klutter.gradle.kts
file like so:

```groovy

def kProps = new Properties()
   new File(project.projectDir, ".klutter/klutter.properties")
           .getCanonicalFile()
           .withReader('UTF-8') { reader -> kProps.load(reader)}

    repositories {
        maven {
            url = uri(kProps.getProperty('private.repo.url'))
            credentials {
                username = kProps.getProperty('private.repo.username')
                password = kProps.getProperty('private.repo.password')
            }
        }
    }

```

### Task: generateAdapter

The generatedAdapter task creates all the boilerplate code needed to make the Dart code in Flutter 
communicate with Kotlin in the Multiplatform module.
- //todo explan methodchannels and typesafety through protobuff
- //link to the generateServices task
- //edit the example to latest code

```kotlin
plugins {
    id("dev.buijs.klutter.plugins.adapter")
}

klutter {
    
    multiplatform {
        source = "myproject/kmp/common/src/commonMain"
    } 
    
    root = "root/flutterproj/lib"
    android = "root/flutterproj/android/app"
    ios = "root/flutterproj/ios"
}

dependencies {
    implementation("dev.buijs.klutter.plugins:adapter:1.0.0")
}

```

#### Place annotations
The MainActivity in the flutter/android/app source should be annotated with the @KlutterAdapter annotation.
This will enable the plugin to find the file and add all the needed methods to call into KMP.
The MainActivity will handle all MethodChannel calls by delegating the request to the GeneratedKlutterAdapter code.
Next annotate all corresponding methods in the KMP module with @KlutterAdaptee and give it a corresponding name.

For example this method in your KMP module:

```kotlin

package dev.foo.bar
        
class MyClass {
    @KlutterAdaptee(name = "doPlatformCall")
    fun somePlatformMethod(): String {
        return doSomething().getSomeValue
    }
}

```

Will generate this code and add it to the GeneratedKlutterAdapter class:

```kotlin

    if (call.method == "doPlatformCall") {
        result.success("${dev.foo.bar.MyClass().somePlatformMethod()}")
    }

```




### Task: generateAndroidBuildGradle

### Task: generateApi

### Task: buildDebug




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