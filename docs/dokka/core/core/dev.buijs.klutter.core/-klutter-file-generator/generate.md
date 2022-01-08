---
title: generate
---
//[core](../../../index.html)/[dev.buijs.klutter.core](../index.html)/[KlutterFileGenerator](index.html)/[generate](generate.html)



# generate



[jvm]\
open fun [generate](generate.html)(): [KlutterLogger](../-klutter-logger/index.html)



Use the [printer](../../../../core/dev.buijs.klutter.core/-klutter-file-generator/printer.md) function to create the file content and pass it to the [writer](../../../../core/dev.buijs.klutter.core/-klutter-file-generator/writer.md) fun to create a new file.



If this method is not overridden it will function under the assumption that the [writer](../../../../core/dev.buijs.klutter.core/-klutter-file-generator/writer.md) function calls the [printer](../../../../core/dev.buijs.klutter.core/-klutter-file-generator/printer.md) function and writes it output to a location specified in the [KlutterWriter](../../../../core/dev.buijs.klutter.core/-klutter-writer/index.md) class.



#### Return



logger with info about the steps done by the generator.




