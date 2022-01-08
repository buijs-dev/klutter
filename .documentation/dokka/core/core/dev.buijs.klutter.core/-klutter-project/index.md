---
title: KlutterProject
---
//[core](../../../index.html)/[dev.buijs.klutter.core](../index.html)/[KlutterProject](index.html)



# KlutterProject



[jvm]\
data class [KlutterProject](index.html)(root: [Root](../-root/index.html), ios: [IOS](../-i-o-s/index.html), android: [Android](../-android/index.html), flutter: [Flutter](../-flutter/index.html), kmp: [KMP](../-k-m-p/index.html), klutter: [Klutter](../-klutter/index.html))

A representation of the structure of a project made with the Klutter Framework. Each property of this object represents a folder containing one or more folders and/or files wich are in some way used or needed by Klutter.



#### Author



Gillian Buijs



## Constructors


| | |
|---|---|
| [KlutterProject](-klutter-project.html) | [jvm]<br>fun [KlutterProject](-klutter-project.html)(root: [Root](../-root/index.html), ios: [IOS](../-i-o-s/index.html), android: [Android](../-android/index.html), flutter: [Flutter](../-flutter/index.html), kmp: [KMP](../-k-m-p/index.html), klutter: [Klutter](../-klutter/index.html)) |


## Properties


| Name | Summary |
|---|---|
| [android](android.html) | [jvm]<br>val [android](android.html): [Android](../-android/index.html)<br>is the folder containing the Android frontend code, basically the iOS folder from a standard Flutter project. |
| [flutter](flutter.html) | [jvm]<br>val [flutter](flutter.html): [Flutter](../-flutter/index.html)<br>is the lib folder containing the main.dart, the starting point of the Android/iOS application. |
| [ios](ios.html) | [jvm]<br>val [ios](ios.html): [IOS](../-i-o-s/index.html)<br>is the folder containing the iOS frontend code, basically the iOS folder from a standard Flutter project. |
| [klutter](klutter.html) | [jvm]<br>val [klutter](klutter.html): [Klutter](../-klutter/index.html)<br>is the folder containing all Klutter configuration and the configured Klutter Plugin. |
| [kmp](kmp.html) | [jvm]<br>val [kmp](kmp.html): [KMP](../-k-m-p/index.html)<br>is the folder containing the native backend code, basically a Kotlin Multiplatform library module. |
| [root](root.html) | [jvm]<br>val [root](root.html): [Root](../-root/index.html)<br>is the top level of the project. |

