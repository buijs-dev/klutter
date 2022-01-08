---
title: IOS
---
//[core](../../../index.html)/[dev.buijs.klutter.core](../index.html)/[IOS](index.html)



# IOS



[jvm]\
class [IOS](index.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)?, root: [Root](../-root/index.html)) : [KlutterFolder](../-klutter-folder/index.html)

If no custom path is given, Klutter assumes the path to the iOS module is [root](../../../../core/dev.buijs.klutter.core/-i-o-s/root.md)/ios.



## Constructors


| | |
|---|---|
| [IOS](-i-o-s.html) | [jvm]<br>fun [IOS](-i-o-s.html)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)? = null, root: [Root](../-root/index.html)) |


## Functions


| Name | Summary |
|---|---|
| [appDelegate](app-delegate.html) | [jvm]<br>fun [appDelegate](app-delegate.html)(): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)<br>If no custom path is given, Klutter assumes the path to the iOS AppDelegate.swfit is root-project/ios/Runner/AppDelegate.swift. |
| [podfile](podfile.html) | [jvm]<br>fun [podfile](podfile.html)(): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)<br>If no custom path is given, Klutter assumes the path to the iOS Podfile is root-project/ios/PodFile. |


## Properties


| Name | Summary |
|---|---|
| [file](../-klutter-folder/file.html) | [jvm]<br>val [file](../-klutter-folder/file.html): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html) |
| [root](../-klutter-folder/root.html) | [jvm]<br>val [root](../-klutter-folder/root.html): [Root](../-root/index.html) |

