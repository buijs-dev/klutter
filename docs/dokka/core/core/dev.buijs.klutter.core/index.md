---
title: dev.buijs.klutter.core
---
//[core](../../index.html)/[dev.buijs.klutter.core](index.html)



# Package dev.buijs.klutter.core



## Types


| Name | Summary |
|---|---|
| [Android](-android/index.html) | [jvm]<br>class [Android](-android/index.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)?, root: [Root](-root/index.html)) : [KlutterFolder](-klutter-folder/index.html)<br>If no custom path is given, Klutter assumes the path to the Android module is [root](../../../core/dev.buijs.klutter.core/-android/root.md)/android. |
| [DartType](-dart-type/index.html) | [jvm]<br>enum [DartType](-dart-type/index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[DartType](-dart-type/index.html)&gt; |
| [Flutter](-flutter/index.html) | [jvm]<br>class [Flutter](-flutter/index.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)?, root: [Root](-root/index.html)) : [KlutterFolder](-klutter-folder/index.html)<br>If no custom path is given, Klutter assumes the path to the flutter lib folder is [root](../../../core/dev.buijs.klutter.core/-flutter/root.md)/lib. |
| [IOS](-i-o-s/index.html) | [jvm]<br>class [IOS](-i-o-s/index.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)?, root: [Root](-root/index.html)) : [KlutterFolder](-klutter-folder/index.html)<br>If no custom path is given, Klutter assumes the path to the iOS module is [root](../../../core/dev.buijs.klutter.core/-i-o-s/root.md)/ios. |
| [Klutter](-klutter/index.html) | [jvm]<br>class [Klutter](-klutter/index.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)?, root: [Root](-root/index.html)) : [KlutterFolder](-klutter-folder/index.html)<br>If no custom path is given, Klutter assumes the path to the klutter folder is [root](../../../core/dev.buijs.klutter.core/-klutter/root.md)/klutter. |
| [KlutterCodeGenerationException](-klutter-code-generation-exception/index.html) | [jvm]<br>class [KlutterCodeGenerationException](-klutter-code-generation-exception/index.html)(msg: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), cause: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Exception](https://docs.oracle.com/javase/8/docs/api/java/lang/Exception.html) |
| [KlutterConfigException](-klutter-config-exception/index.html) | [jvm]<br>class [KlutterConfigException](-klutter-config-exception/index.html)(msg: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), cause: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Exception](https://docs.oracle.com/javase/8/docs/api/java/lang/Exception.html) |
| [KlutterFileGenerator](-klutter-file-generator/index.html) | [jvm]<br>abstract class [KlutterFileGenerator](-klutter-file-generator/index.html)<br>The purpose of this class is to create new files. A KlutterFileGenerator uses a [KlutterPrinter](../../../core/dev.buijs.klutter.core/-klutter-printer/index.md) and a [KlutterWriter](../../../core/dev.buijs.klutter.core/-klutter-writer/index.md) to encapsulate the functionality for creating the content of a file and actually writing the content to a file location. |
| [KlutterFolder](-klutter-folder/index.html) | [jvm]<br>abstract class [KlutterFolder](-klutter-folder/index.html)(root: [Root](-root/index.html), maybeFile: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)?, whichFolder: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), defaultLocation: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)) |
| [KlutterGradleException](-klutter-gradle-exception/index.html) | [jvm]<br>class [KlutterGradleException](-klutter-gradle-exception/index.html)(msg: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), cause: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Exception](https://docs.oracle.com/javase/8/docs/api/java/lang/Exception.html) |
| [KlutterLogger](-klutter-logger/index.html) | [jvm]<br>open class [KlutterLogger](-klutter-logger/index.html) |
| [KlutterLogLevel](-klutter-log-level/index.html) | [jvm]<br>enum [KlutterLogLevel](-klutter-log-level/index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[KlutterLogLevel](-klutter-log-level/index.html)&gt; |
| [KlutterLogMessage](-klutter-log-message/index.html) | [jvm]<br>data class [KlutterLogMessage](-klutter-log-message/index.html)(message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), level: [KlutterLogLevel](-klutter-log-level/index.html)) |
| [KlutterMultiplatformException](-klutter-multiplatform-exception/index.html) | [jvm]<br>class [KlutterMultiplatformException](-klutter-multiplatform-exception/index.html)(msg: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), cause: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Exception](https://docs.oracle.com/javase/8/docs/api/java/lang/Exception.html) |
| [KlutterProducer](-klutter-producer/index.html) | [jvm]<br>interface [KlutterProducer](-klutter-producer/index.html)<br>The purpose of a KlutterProducer is to produce a set of files. |
| [KlutterProject](-klutter-project/index.html) | [jvm]<br>data class [KlutterProject](-klutter-project/index.html)(root: [Root](-root/index.html), ios: [IOS](-i-o-s/index.html), android: [Android](-android/index.html), flutter: [Flutter](-flutter/index.html), kmp: [KMP](-k-m-p/index.html), klutter: [Klutter](-klutter/index.html))<br>A representation of the structure of a project made with the Klutter Framework. Each property of this object represents a folder containing one or more folders and/or files wich are in some way used or needed by Klutter. |
| [KlutterProjectFactory](-klutter-project-factory/index.html) | [jvm]<br>class [KlutterProjectFactory](-klutter-project-factory/index.html)<br>Factory to create a KlutterProject. |
| [KlutterPropertiesReader](-klutter-properties-reader/index.html) | [jvm]<br>class [KlutterPropertiesReader](-klutter-properties-reader/index.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)) |
| [KlutterVisitor](-klutter-visitor/index.html) | [jvm]<br>interface [KlutterVisitor](-klutter-visitor/index.html)<br>Utility interface which processes a given file and may or may not change it's content. |
| [KMP](-k-m-p/index.html) | [jvm]<br>class [KMP](-k-m-p/index.html)(root: [Root](-root/index.html), file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)?, podspecName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), moduleName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), moduleMainName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [KlutterFolder](-klutter-folder/index.html)<br>If no custom path is given, Klutter assumes the path to the KMP module is [root](../../../core/dev.buijs.klutter.core/-k-m-p/root.md)/kmp. |
| [KotlinFileScanningException](-kotlin-file-scanning-exception/index.html) | [jvm]<br>class [KotlinFileScanningException](-kotlin-file-scanning-exception/index.html)(msg: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), cause: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Exception](https://docs.oracle.com/javase/8/docs/api/java/lang/Exception.html) |
| [KotlinToDartPrimitiveMapping](-kotlin-to-dart-primitive-mapping/index.html) | [jvm]<br>class [KotlinToDartPrimitiveMapping](-kotlin-to-dart-primitive-mapping/index.html) |
| [KotlinType](-kotlin-type/index.html) | [jvm]<br>enum [KotlinType](-kotlin-type/index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[KotlinType](-kotlin-type/index.html)&gt; |
| [Root](-root/index.html) | [jvm]<br>class [Root](-root/index.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)) |

