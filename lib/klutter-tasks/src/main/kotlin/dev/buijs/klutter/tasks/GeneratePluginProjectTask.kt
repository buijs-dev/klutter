/* Copyright (c) 2021 - 2022 Buijs Software
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
package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.shared.maybeCreate
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

/**
 * The version of the Klutter Pub Plugin.
 */
const val klutterPubVersion = "0.3.0"

/**
 * Task to generate a klutter plugin project.
 */
class GeneratePluginProjectTask(
    /**
     * Path to the folder where to create the new project.
     */
    private val pathToRoot: String,

    /**
     * Name of the plugin.
     */
    private val pluginName: String,

    /**
     * Name of the plugin organisation.
     */
    private val groupName: String,

    /**
     * Utility to execute the flutter commands.
     *
     * Can be swapped for testing purposes.
     */
    private val executor: CliExecutor = CliExecutor(),

    ) : KlutterTask {

    override fun run() {
        File(pathToRoot).verifyExists().createApp()
    }

    private fun File.createApp() {
        "flutter create $pluginName --org $groupName --template=plugin --platforms=android,ios".execute(this)

        val pluginFolder = resolve(pluginName)
        val exampleFolder = resolve(pluginName).resolve("example")
        val pubspecs = listOf(
            pluginFolder.resolve("pubspec.yaml"),
            exampleFolder.resolve("pubspec.yaml"),
        )

        for(pubspec in pubspecs) {
            val updated = mutableListOf<String>().also {
                pubspec.verifyExists().readLines().forEach { line ->
                    if(!line.contains("plugin_platform_interface:")) {
                        it.add(line)
                    }

                    if (line.trim().startsWith("dependencies:")) {
                        it.add("  klutter: ^$klutterPubVersion")
                    }
                }
            }
            pubspec.writeText(updated.joinToString("\n"))
        }

        "flutter pub get".execute(pluginFolder)
        "flutter pub get".execute(exampleFolder)
        "flutter pub run klutter:producer init".execute(pluginFolder)
        "flutter pub run klutter:consumer init".execute(exampleFolder)
        "flutter pub run klutter:producer install=library".execute(pluginFolder)

        pluginFolder.resolve("android/local.properties")
            .copyTo(pluginFolder.resolve("local.properties"))

        // You should test, but we're going to do that with Spock/JUnit
        // in the platform module, not with Dart in the root/test folder.
        pluginFolder.resolve("test").deleteRecursively()

        // Change the README content to explain Klutter plugin development.
        pluginFolder.resolve("README.md").let { readme ->

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

    /**
     * Execute a CLI command in the given folder.
     */
    private fun String.execute(runFrom: File) =
        executor.execute(runFrom = runFrom, command = this)
}