---
title: KlutterFileGenerator
---
//[core](../../../index.html)/[dev.buijs.klutter.core](../index.html)/[KlutterFileGenerator](index.html)



# KlutterFileGenerator



[jvm]\
abstract class [KlutterFileGenerator](index.html)

The purpose of this class is to create new files. A KlutterFileGenerator uses a [KlutterPrinter](../../../../core/dev.buijs.klutter.core/-klutter-printer/index.md) and a [KlutterWriter](../../../../core/dev.buijs.klutter.core/-klutter-writer/index.md) to encapsulate the functionality for creating the content of a file and actually writing the content to a file location.



## Constructors


| | |
|---|---|
| [KlutterFileGenerator](-klutter-file-generator.html) | [jvm]<br>fun [KlutterFileGenerator](-klutter-file-generator.html)() |


## Functions


| Name | Summary |
|---|---|
| [generate](generate.html) | [jvm]<br>open fun [generate](generate.html)(): [KlutterLogger](../-klutter-logger/index.html)<br>Use the [printer](../../../../core/dev.buijs.klutter.core/-klutter-file-generator/printer.md) function to create the file content and pass it to the [writer](../../../../core/dev.buijs.klutter.core/-klutter-file-generator/writer.md) fun to create a new file. |


## Inheritors


| Name |
|---|
| [KlutterGradleFileGenerator](../../dev.buijs.klutter.core.config/-klutter-gradle-file-generator/index.html) |
| [KlutterPropertiesGenerator](../../dev.buijs.klutter.core.config/-klutter-properties-generator/index.html) |

