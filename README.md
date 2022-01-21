![This is an image](example-basic/assets/metadata/icon/banner.png)

# Klutter
[![GitHub license](https://img.shields.io/github/license/buijs-dev/klutter)](#License)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/dev.buijs.klutter.gradle?color=blueviolet)](https://plugins.gradle.org/plugin/dev.buijs.klutter.gradle)
[![Repsy maven](https://img.shields.io/badge/maven-2022--pre--alpha--1-blue)](https://repsy.io/mvn/buijs-dev/klutter/)


Klutter is a framework and tool set which uses Flutter to create the frontend
and Kotlin Multiplatform for the backend. The connective layer is generated
by the Klutter framework. Klutter combines industry best practices
for everything from app design to CICD into a single cohesive framework.

# Klutter is in PRE-ALPHA stage!
[Documentation](https://buijs-dev.github.io/klutter/)
Roadmap

### Code generation
* [x] Generate android build.gradle in root/android and root/android/app
* [x] Generate PodFile in root/ios and AppDelegate.swift in ios/Runner 
* [x] Generate adapter.dart in root/lib/generated
* [x] Read annotations from KMP source and generate simple method calls
* [x] Generate PodFile in root/ios and AppDelegate.swift in ios/Runner
* [ ] Read annotations from KMP source and generate method calls that take args

### Example apps
* [x] [example-basic](example-basic) Add basic project which works on iOS and Android
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
  - generate api
  - generate adapter
  - build debug

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

More info: [Configuration management in Klutter](docs/doc_configuration_management.md)

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

### Task: generate adapter

The generatedAdapter task creates all the boilerplate code needed to make the Dart code in Flutter 
communicate with Kotlin in the Multiplatform module.

```kotlin
plugins {
    id("dev.buijs.klutter.gradle")
}

klutter {
    
    multiplatform {
        source = "kmp/common/src/commonMain"
    } 
    
}

dependencies {
    implementation("dev.buijs.klutter.plugins:adapter:1.0.0")
}

```

#### Place annotations
There are 3 annotations:
- KlutterAdapter
- KlutterAdaptee
- KlutterResponse

**KlutterAdapter**\
The MainActivity in the flutter/android/app source should be annotated with the **@KlutterAdapter** annotation.
This will enable the plugin to find the file and add all the needed methods to call into KMP.
The MainActivity will handle all MethodChannel calls by delegating the request to the GeneratedKlutterAdapter code.


**KlutterAdaptee**\
All corresponding methods in the KMP module should be annotated with **@KlutterAdaptee** and given a corresponding name.
All methods annotated with this annotation are added to the GeneratedKlutterAdapter. In other words: Adding this annotation
to a method in KMP will make it visible for the Flutter.


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


**KlutterResponse**\
This annotation enables KMP and Flutter to communicate using data transfer objects instead of Strings.
The KlutterResponse can be used to annotate a simple DTO after which Klutter will generate an equivalent 
Dart DTO with all boilerplate code to (de)serialize.

The annotated class should comply with the following rules:

1. Must be an open class
2. Fields must be immutable
3. Must implement KlutterJSON class
4. No additional functionality implemented in body
5. Any field type should comply with the same rules

**Note:** Extending the KlutterJSON class might be no longer needed if a compiler plugin is created.

A KlutterResponse acts as an interface between Flutter and KMP. These rules are designed to adhere to that function.

Open classes can be extended so the DTO can be used as interface between KMP and Flutter and you can extend it
to add behaviour designed for frontend or backend respectively. All fields must be immutable. The generated code includes
builders to create a new instance of the DTO if needed. Make sure to declare fields in the DTO as <i>val</i> and not var.
Any behaviour should be written in subclasses. To avoid any unnecessary complexity it may not inherit any fields/behaviour from other classes.
This is a functional design choise, not a technical limitation.

**Supported Kotlin datatypes**
1. Int
2. Double
3. Boolean
4. List

**Maps?**
Maps are currently not supported. A DTO is a better/safer option by providing typesafety e.a.

**Enumerations?**
Enumerations can be used as datatype but only if the enumeration itself has a no-args constructor.
Values should be defined in UPPER_SNAKE_CASE. Klutter will convert it to lowerCamelCase for usage in Dart/Flutter.
The value "none" is a reserved value used to represent null. 

**Custom data types?**
Any field declaration may use another DTO as type but that DTO should comply with before mentioned rules as well.

**What could possibly go wrong?**
Any class annotated with KlutterResponse that does not comply will be logged as error and ignored for processing. 
Any other dependent class will also be ignored as result.

**Requirements**
To serialize the KlutterResponse kotlinx serialization is used. Add the plugin to the KMP build.gradle.kts:

````kotlin
plugins {
    kotlin("plugin.serialization") version "<use-project-kotlin-version>"
}
````

Also add the json dependency to the commonMain sourceset:

```kotlin
 val commonMain by getting {
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    }
}
```

**Examples**

Example of valid declaration:

```kotlin

    @Serializable
    @KlutterResponse
    open class Something(
        val x: String?,
        val y: SomethingElse
    ): KlutterJSON<Something>() {

        override fun data() = this

        override fun strategy() = serializer()

    }

    @Serializable
    @KlutterResponse
    open class SomethingElse(
        val a: Int?,
        val b: List<Boolean>
    ): KlutterJSON<SomethingElse>() {

        override fun data() = this

        override fun strategy() = serializer()

    }

```
<br />

Example of invalid declaration (Mutability):

```kotlin

    @Serializable
    @KlutterResponse
    open class Something(
        var x: String?,
        var y: Int,
    ): KlutterJSON<SomethingElse>() {

        override fun data() = this

        override fun strategy() = serializer()

    }

```
<br />

Example of invalid declaration (SomethingElse class should not have a body):

```kotlin

    @Serializable
    @KlutterResponse
    open class Something(
        val x: String?,
        var y: SomethingElse
    ): KlutterJSON<SomethingElse>() {

        override fun data() = this

        override fun strategy() = serializer()

    }

    @Serializable
    @KlutterResponse
    open class SomethingElse(
        val a: Int?,
        val b: List<Boolean>
    ): KlutterJSON<SomethingElse>() {

        val bodyNotAllowed: Boolean = true
        
        override fun data() = this

        override fun strategy() = serializer()

    }

```
<br />

### Task: build debug
This task builds the KMP library, renames the created .aar file to kmp.aar and copies 
it from my-project/kmp/common/build/outputs/aar to root-project/.klutter folder so that
the android/app build.gradle can include this .aar file as a local dependency. Note: This build.gradle
file is generated by Klutter. For more information see [task 'generate adapter'](#Task:-generate-api) for how the build.gradle is generated.

Building the KMP library should also generate a .podspec file, which the iOS module will depend on.
See before mentioned chapter [task 'generate adapter'](#Task:-generate-api) for how Klutter ensures the iOS module can find the .podspec.

Next the Pods and Podfile.lock in my-project/ios are deleted and Pod update is executed to retrieve the newly created
podspec. The deletion is necessary to be sure the new podspec is used.
Klutter will output all logging from the dependend tasks. Always check the log to see if the build was succesfull. 

Finally the app can be run for iOS and/or Android by running the lib/main.dart file on an emulator/simulator.


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
