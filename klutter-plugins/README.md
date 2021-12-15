# Klutter Adapter Gradle Plugin

The KlutterAdapterPlugin generates the MethodChannel code used by the Flutter frontend to communicate with Kotlin backend.

## Usage

#### Configure plugin
Apply the plugin in the root build.gradle.kts file and specify the required configuration. 
Sources: List of String containing the package(s) which contain the methods to be called by the adapter. 
Only methods annoted with the KlutterAdaptee annotation are scanned.
Flutter: File instance pointing to the flutter/lib folder which contains the main.dart file.
Android: File instance pointing to the flutter/android/app folder.
Ios: File instance pointing to the flutter/ios folder.

Lastly you need the klutter-core dependency.

```kotlin
plugins {
    id("dev.buijs.klutter.plugins.adapter")
}

klutteradapter {
    sources = listOf("root/kotlinlibrary", "dir2")
    flutter = "root/flutterproj/lib"
    android = "root/flutterproj/android/app"
    ios = "root/flutterproj/ios"
}

dependencies {
    implementation("dev.buijs.klutter.plugins:adapter:1.0.0")
}

```

#### Implement KlutterActivity
In your flutter/android/app folder change your MainActivity to not extend the FlutterActivity but KlutterActivity.
The KlutterActivity will handle all MethodChannel calls by delegating the request to the GeneratedAdapter code.

#### Annotate methods 
The plugin will scan for methods annotated with KlutterAdaptee. For each annotated method, a code block will be added to the
GeneratedAdapter code. 

For example this method:

```kotlin

package dev.foo.bar
        
class MyClass {
    @KlutterAdaptee(name = "someMethodCall")
    fun somePlatformMethod(): String {
        return doSomething().getSomeValue
    }
}

```

Will generate this code and add it to the GeneratedKlutterAdapter class:

```kotlin

    if (call.method == "someMethodCall") {
        result.success("${dev.foo.bar.MyClass().somePlatformMethod()}")
    }

```

