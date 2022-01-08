---
title: Klutter: Core
---

# Klutter: Core
The core module contains all the essential Klutter functionality.

## Packages

| Name |
|---|
| [dev.buijs.klutter.core](core/dev.buijs.klutter.core/index.md) |
| [dev.buijs.klutter.core.adapter](core/dev.buijs.klutter.core.adapter/index.md) |
| [dev.buijs.klutter.core.config](core/dev.buijs.klutter.core.config/index.md) |
| [dev.buijs.klutter.core.flutter](core/dev.buijs.klutter.core.flutter/index.md) |

### Core
- [KlutterExceptions](core/dev.buijs.klutter.core/index.md)
- [KlutterInterfaces](core/dev.buijs.klutter.core/index.md)
- [KlutterLogger](core/dev.buijs.klutter.core/-klutter-logger/index.md)
- [KlutterProject](core/dev.buijs.klutter.core/-klutter-project/index.md)

### Core - Adapter
Facade for Gradle AdapterTask which delegates creation and editing to specific classes.

[KlutterAdapterProducer](core/dev.buijs.klutter.core.adapter/-klutter-adapter-producer/-klutter-adapter-producer.md)

### Core - Config
Config package encapsulates all functionality to read yaml files and generate config files.

### Core - Flutter
The flutter package contains all classes to create or edit files in the flutter folders.
These include:
- lib
- android
- android/app
- ios