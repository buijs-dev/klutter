---
title: KMP
---
//[core](../../../index.html)/[dev.buijs.klutter.core](../index.html)/[KMP](index.html)



# KMP



[jvm]\
class [KMP](index.html)(root: [Root](../-root/index.html), file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)?, podspecName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), moduleName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), moduleMainName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [KlutterFolder](../-klutter-folder/index.html)

If no custom path is given, Klutter assumes the path to the KMP module is [root](../../../../core/dev.buijs.klutter.core/-k-m-p/root.md)/kmp.



## Constructors


| | |
|---|---|
| [KMP](-k-m-p.html) | [jvm]<br>fun [KMP](-k-m-p.html)(root: [Root](../-root/index.html), file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)? = null, podspecName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = "common.podspec", moduleName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = "common", moduleMainName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = "commonMain") |


## Functions


| Name | Summary |
|---|---|
| [build](build.html) | [jvm]<br>fun [build](build.html)(): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)<br>If no custom path is given, Klutter assumes the path to the KMP build folder is root-project/kmp/common/build. |
| [module](module.html) | [jvm]<br>fun [module](module.html)(): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)<br>If no custom path is given, Klutter assumes the path to the KMP module is root-project/kmp/common. |
| [moduleName](module-name.html) | [jvm]<br>fun [moduleName](module-name.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [podspec](podspec.html) | [jvm]<br>fun [podspec](podspec.html)(): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)<br>If no custom path is given, Klutter assumes the path to the KMP sourcecode is root-project/kmp/common/commmon.podspec. |
| [source](source.html) | [jvm]<br>fun [source](source.html)(): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)<br>If no custom path is given, Klutter assumes the path to the KMP sourcecode is root-project/kmp/common/src/commonMain. |


## Properties


| Name | Summary |
|---|---|
| [file](../-klutter-folder/file.html) | [jvm]<br>val [file](../-klutter-folder/file.html): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html) |
| [root](../-klutter-folder/root.html) | [jvm]<br>val [root](../-klutter-folder/root.html): [Root](../-root/index.html) |

