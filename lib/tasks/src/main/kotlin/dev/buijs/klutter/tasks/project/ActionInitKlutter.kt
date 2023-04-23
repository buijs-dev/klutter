/* Copyright (c) 2021 - 2023 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package dev.buijs.klutter.tasks.project

import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.tasks.execute
import dev.buijs.klutter.tasks.input.PluginName
import dev.buijs.klutter.tasks.input.RootFolder
import dev.buijs.klutter.tasks.input.validPluginNameOrThrow
import dev.buijs.klutter.tasks.input.validRootFolderOrThrow

internal fun ProjectBuilderOptions.toInitKlutterAction() =
    InitKlutter(rootFolder, pluginName)

internal class InitKlutter(
    private val rootFolder: RootFolder,
    private val pluginName: PluginName,
): ProjectBuilderAction {

    override fun doAction() {

        val pluginName = pluginName.validPluginNameOrThrow()

        val rootFolder = rootFolder.validRootFolderOrThrow().resolve(pluginName)

        val exampleFolder = rootFolder.resolve("example")

        val rootPubspecFile = rootFolder.resolve("pubspec.yaml")

        val rootPubspec = rootPubspecFile.toPubspec()

        val klutterConfigFile = rootFolder.resolve("klutter.yaml")
        val klutterConfig = if(klutterConfigFile.exists()) klutterConfigFile.toKlutterConfig() else null

        val examplePubspecFile = exampleFolder.resolve("pubspec.yaml")

        rootPubspecInit(
            pubspec = rootPubspec,
            pubspecFile = rootPubspecFile,
            config = klutterConfig)

        examplePubspecInit(
            rootPubspec = rootPubspec,
            examplePubspecFile = examplePubspecFile,
            config = klutterConfig)

        println("root~")
        println(rootPubspecFile.readText())
        println("example~")
        println(examplePubspecFile.readText())
        "flutter pub get" execute rootFolder
        "flutter pub get" execute exampleFolder
        "flutter pub run klutter:producer init" execute rootFolder
        "flutter pub run klutter:consumer init" execute exampleFolder

        val properties = rootFolder.resolve("local.properties")
        if(!properties.exists())
            rootFolder.resolve("android/local.properties").copyTo(properties)

        // You should test, but we're going to do that with Spock/JUnit
        // in the platform module, not with Dart in the root/test folder.
        rootFolder.resolve("test").deleteRecursively()

        // Change the README content to explain Klutter plugin development.
        rootFolder.resolve("README.md").let { readme ->
            // Seems redundant to delete and then recreate the README.md file.
            // However, the project is created by invoking the Flutter version
            // installed by the end-user. Future versions of Flutter might not
            // Create a README.md file but a readme.md, PLEASE_README.md or not
            // a readme file at all!
            //
            // In that case this code won't try to delete a file that does not
            // exist and only create a new file. We do NOT want the task to fail
            // because of a README.md file now, do we? :-)
            if(readme.exists()) readme.delete()
            readme.createNewFile()
            readme.writeText("""
                |# $pluginName
                |A new Klutter plugin project. 
                |Klutter is a framework which interconnects Flutter and Kotlin Multiplatform.
                |
                |## Getting Started
                |This project is a starting point for a Klutter
                |[plug-in package](https://github.com/buijs-dev/klutter),
                |a specialized package that includes platform-specific implementation code for
                |Android and/or iOS. 
                |
                |This platform-specific code is written in Kotlin programming language by using
                |Kotlin Multiplatform. 
            """.trimMargin())
        }
    }

}