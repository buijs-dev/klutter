# Kradle

Executable tool to create a new independent [Klutter](https://github.com/buijs-dev/klutter) project.
Create a new project with the click of a button without having any frameworks installed (including Flutter).

Kradle can be used on any platform using the kraldew or kradlew.bat script.
Running your Gradle and/or Flutter commands through the kradle-wrapper 
ensures you use the correct versions for these libraries. You can send commands
through the kradle-wrapper directly or use the interactive wizard to configure.

## Commands
- [kradle](#kradle)
- [gradle](#gradle)
- [flutter](#flutter)

### Kradle
Kradle commands:
- build
- [clean](#clean)
- [create](#create)
- [get](#get)

#### Build
Build the iOS and Android artifacts and generate all boilerplate code.

> Build is always required to run the app on a device when changes are done in the platform module.

Example:
```shell
./kradlew build
```

#### Clean
Options:
- [cache](#cache)

##### Cache
Remove all files and/or folders from the kradle cache folder.
The cache folder is by default set to user.home/.kradle/cache.
It can be overwritten by setting the cache property in kradle.env.
The kradle.env is stored next to the kradlew scripts.

Default kradle.env cache setting:
```properties
cache={{system.user.home}}/.kradle/cache/
```

Example command:
```shell
./kradlew clean cache
```

#### Create
Create a new Klutter project.

Required arguments:
- root: The root folder of the project.
- name: The name of the project (and subsequently the Flutter plugin).
- group: The group/organisation name of the project.
- flutter: The Flutter distribution to use.

Example:
```shell
./kradlew create --root "./" --name "my_plugin" --group "com.example" --flutter "3.10.6.macos.arm64"
```

##### Config (Optional)
Path to config yaml. This yaml can be used to configure project versions and dependencies. 

Example yaml:
```yaml
bom-version: "2023.3.1.beta"
flutter-version: "3.10.6"
dependencies:
    klutter: "2.0.0"
    klutter_ui: "1.0.1"
    squint: "0.1.2"
    embedded:
       - "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
```

Example command:
```shell
./kradlew create --config "./foo/bar/kradle.yaml" --root "./" --name "my_plugin" --group "com.example" --flutter "3.10.6.macos.arm64"
```

#### Get
Get project dependencies and store them in the kradle cache.
Options:
- [flutter](#get-flutter)

##### Get Flutter
Get a Flutter distribution which is compatible with Klutter.
Options:
- [dist](#Dist)
- [overwrite](#Overwrite)

###### Dist
The distribution to download in format major.minor.patch.platform.architecture.

Example command:
```shell
./kradlew get flutter --dist "3.10.6.windows.x64"
```

###### Overwrite
Overwrite any existing distribution if present.

Example which overwrites any existing distribution:
```shell
./kradlew get flutter --dist "3.10.6.windows.x64" --overwrite
```

### Gradle
Gradle commands can be executed by using the -g argument.

```shell
# Run gradle clean build in the folder /platform.
./kradlew -g clean build -p "platform"
```

### Flutter
Flutter commands can be executed by using the -f argument.

```shell
# Run flutter doctor command.
./kradlew -f doctor
```

## Wizard
When no argument is given then the interactive wizard is started.

```shell
./kradlew
```


// TODO wizard UI options etc.