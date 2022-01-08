---
title: YamlProcesStep
---
//[core](../../../index.html)/[dev.buijs.klutter.core.config](../index.html)/[YamlProcesStep](index.html)



# YamlProcesStep



[jvm]\
abstract class [YamlProcesStep](index.html)

Superclass to control processing of YAML file.



## Constructors


| | |
|---|---|
| [YamlProcesStep](-yaml-proces-step.html) | [jvm]<br>fun [YamlProcesStep](-yaml-proces-step.html)() |


## Functions


| Name | Summary |
|---|---|
| [execute](execute.html) | [jvm]<br>abstract fun [execute](execute.html)(): [YamlProcesStep](index.html)? |
| [getLogger](get-logger.html) | [jvm]<br>fun [getLogger](get-logger.html)(): [KlutterLogger](../../dev.buijs.klutter.core/-klutter-logger/index.html) |
| [hasNext](has-next.html) | [jvm]<br>abstract fun [hasNext](has-next.html)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [property](property.html) | [jvm]<br>open fun [property](property.html)(): [YamlProperty](../-yaml-property/index.html)? |
| [setLogger](set-logger.html) | [jvm]<br>fun [setLogger](set-logger.html)(logger: [KlutterLogger](../../dev.buijs.klutter.core/-klutter-logger/index.html)) |


## Inheritors


| Name |
|---|
| [YamlProcesStepStart](../-yaml-proces-step-start/index.html) |
| [YamlProcesEmptyLine](../-yaml-proces-empty-line/index.html) |
| [YamlProcesSplitLine](../-yaml-proces-split-line/index.html) |
| [YamlProcesAsSubHeader](../-yaml-proces-as-sub-header/index.html) |
| [YamlProcesCreateProperty](../-yaml-proces-create-property/index.html) |
| [YamlProcesNewBlock](../-yaml-proces-new-block/index.html) |
| [YamlProcesStepSuccess](../-yaml-proces-step-success/index.html) |
| [YamlProcesStepFailed](../-yaml-proces-step-failed/index.html) |

