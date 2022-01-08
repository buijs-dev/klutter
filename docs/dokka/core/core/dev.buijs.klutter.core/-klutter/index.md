---
title: Klutter
---
//[core](../../../index.html)/[dev.buijs.klutter.core](../index.html)/[Klutter](index.html)



# Klutter



[jvm]\
class [Klutter](index.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)?, root: [Root](../-root/index.html)) : [KlutterFolder](../-klutter-folder/index.html)

If no custom path is given, Klutter assumes the path to the klutter folder is [root](../../../../core/dev.buijs.klutter.core/-klutter/root.md)/klutter.



## Constructors


| | |
|---|---|
| [Klutter](-klutter.html) | [jvm]<br>fun [Klutter](-klutter.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)? = null, root: [Root](../-root/index.html)) |


## Types


| Name | Summary |
|---|---|
| [KlutterYaml](-klutter-yaml/index.html) | [jvm]<br>enum [KlutterYaml](-klutter-yaml/index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[Klutter.KlutterYaml](-klutter-yaml/index.html)&gt; |


## Functions


| Name | Summary |
|---|---|
| [yaml](yaml.html) | [jvm]<br>fun [yaml](yaml.html)(which: [Klutter.KlutterYaml](-klutter-yaml/index.html)): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)<br>Assumes all yaml files for klutter configuration are stored in [root](../../../../core/dev.buijs.klutter.core/-klutter/root.md)/klutter. |


## Properties


| Name | Summary |
|---|---|
| [file](../-klutter-folder/file.html) | [jvm]<br>val [file](../-klutter-folder/file.html): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html) |
| [root](../-klutter-folder/root.html) | [jvm]<br>val [root](../-klutter-folder/root.html): [Root](../-root/index.html) |

