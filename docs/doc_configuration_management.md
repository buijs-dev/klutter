# Configuration management in Klutter

Klutter has it's own way of managing project configuration. Here we talk about:
1. [Underlaying principles of Klutter configuration management](#Underlaying-principles-of-Klutter-configuration-management)
2. [Brief summary of usage](#Brief-summary-of-usage)


## Underlaying principles of Klutter configuration management
Gradle offers multiple ways of management like included builds (buildSrc) and composed builds. 
There are some benefits of using this, typesafety being one of the most important. The downside is you 
can not always use these typesafe classes and you are forced to include all sub modules in the Gradle build. 
The main purpose of Klutter is to connect different technologies/ecosystems. Instead of trying to 
connect these distinct ecosystems directly it makes sense to keep them intact but feed them the same
configuration. 

Klutter succeeds in doing this by acting as a facade for Flutter and KMP. The main requirements 
for configuration management through Klutter are as follows:

1. Single source of truth.
2. Easy to use.
3. Easy to maintain.

### Single source of truth
Klutter should provide all configuration information for the entire project in a single source.
(Sub)modules should get the information the need from the single Klutter source.

Why? Another principle is DRY: 'Don't repeat yourself'. To build an app with Flutter and KMP means having dependencies 
on a lot of different locations. Even the simplest form of a Klutter app will have Kotlin dependencies defined in the
KMP root module, KMP common module, Flutter Android module, Android App module, etc. Building against different
versions of the same dependency can cause a plethora of issues and should be avoided. Need to pull a dependency
from a private repository? You would not want to manually add the repository configuration and credentials to 
all the modules that need it. Don't repeat yourself. You also want to make sure the entire project uses the same
configuration: Single source of truth.

### Easy to use
Klutter should cater to the different technological layers and provide the configuration in a form that can be used easily.

Why? Once the SSOT principle is adhered too, it's just a matter of form.

### Easy to maintain
Any change in configuration should be propagated to all consumers.

Why? A change in the SSOT should update all dependens. If this is not guaranteed then there is no certainty that the
entire project is compliant, meaning dependency issues can arise or worse.


# Brief summary of usage
Klutter differentiates between public and private project information.

- [public](#public)
- [private](#private)

### Public 
BuildSrc and included builds are commonly used to manage dependencies, plugins etc. in Gradle projects.
Unfortunately it is not possible to access the buildSrc and/or any included builds from the Flutter/Android project.
As discussed above having a single source of truth is a hard requirement. Klutter therefore generates a klutter.gradle
file which does nothing more than hold a set of properties. This gradle file can then be either be applied by 
other Gradle modules (the platform module, android module, etc) or even just read as text file. 

A new generated Klutter project will for example contain the following boilerplate in the Platform build.gradle:

```kotlin

apply(from = "${project.projectDir}/../klutter.gradle")

val applicationId: String by extra
val appVersionName: String by extra
val kotlinxVersion: String by extra
val klutterVersion: String by extra
val junitVersion: String by extra
val androidCompileSdk: Int by extra
val androidMinSdk: Int by extra
val androidTargetSdk: Int by extra
val iosVersion: String by extra


```

And in the Android build.gradle:

```groovy

    def klutterGradleFile = new File("${projectDir}/../klutter.gradle")
    if (!klutterGradleFile.exists()) {
        throw new GradleException("File not found $klutterGradleFile")
    }

    apply from: "$klutterGradleFile"


```

Changing the android compile SDK for example is as simple as updating this property in the klutter.gradle file
in the root folder.

The default Klutter project contains a buildSrc folder which adds all necessary libraries to the classpath.
For more type safety it is possible to also create classes in the buildSrc module which would read the klutter.gradle file
and return it's information through type safe getters. 

### Private

Use Klutter secrets utility to load private properties from the klutter.secrets file or env variables. The utility
will load the klutter.secrets file and look for a key and if not found fallback to env variables. Key name is 
converted to UPPER_CAMEL_CASE when looking in the env variables. 

Example:

```groovy
import dev.buijs.klutter.core.*

def secrets = Klutter.secrets(project)

signingConfigs {
    release {
        storeFile file(secrets.get("store.file.uri") ?: project.projectDir)
        storePassword secrets.get("store.password") ?: ""
        keyAlias secrets.get("key.alias") ?: ""
        keyPassword secrets.get("key.password") ?: ""
    }
}
```

Some information should be kept private like API keys for example. Environment variables are commonly used for this. 
I'm personally not a huge fan of using environment variables for everything. I prefer to put information in files and 
store them. Of course you should never store private information as-is in your GIT repository (not even private ones)!
Luckily there are ways to safely store private information by encrypting it. [Git secret](https://git-secret.io/) for 
example enables you to encrypt/decrypt files automatically when committing to GIT. 

Klutter secrets gives you the option to use whichever you prefer, properties file or env variables (or both) 
without having to change the way to access it.