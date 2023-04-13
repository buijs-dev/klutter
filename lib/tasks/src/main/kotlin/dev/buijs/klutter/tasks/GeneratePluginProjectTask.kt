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
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

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
        File(pathToRoot).verifyExists().let { rootFolder ->
            createProjectCommand executeFrom rootFolder
                .also { initializeProjectTask(it).run() }
        }
    }

    private val createProjectCommand get () = "" +
            "flutter create $pluginName " +
            "--org $groupName " +
            "--template=plugin " +
            "--platforms=android,ios"

    private infix fun initializeProjectTask(rootFolder: File) =
        InitializePluginProjectTask(
            rootFolder = rootFolder.resolve(pluginName),
            pluginName = pluginName,
            executor = executor)

    /**
     * Execute a CLI command in the given folder.
     */
    private infix fun String.executeFrom(runFrom: File) =
        executor.execute(runFrom = runFrom, command = this)
}


/**
 * Task to generate a klutter plugin project.
 */
class InitializePluginProjectTask(

    /**
     * Path to the folder where to create the new project.
     */
    private val rootFolder: File,

    /**
     * Name of the plugin.
     */
    private val pluginName: String,

    /**
     * Utility to execute the flutter commands.
     *
     * Can be swapped for testing purposes.
     */
    private val executor: CliExecutor = CliExecutor(),

    ) : KlutterTask {

    override fun run() {
        rootFolder.verifyExists()
        val exampleFolder = rootFolder.resolve("example")
        val rootPubspecFile = rootFolder.resolve("pubspec.yaml")
        val rootPubspec = rootPubspecFile.toPubspec()

        rootFolder.pubspecInit(
            name = rootPubspec.name ?: "",
            androidPackageName = rootPubspec.androidPackageName(),
            pluginClassName = rootPubspec.androidClassName("")
        )

        "flutter pub get".execute(rootFolder)
        "flutter pub get".execute(exampleFolder)
        "flutter pub run klutter:producer init".execute(rootFolder)
        "flutter pub run klutter:consumer init".execute(exampleFolder)

        rootFolder.resolve("local.properties").let { localProperties ->
            if(!localProperties.exists()) {
                rootFolder.resolve("android/local.properties").copyTo(localProperties)
            }
        }

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

    /**
     * Execute a CLI command in the given folder.
     */
    private fun String.execute(runFrom: File) =
        executor.execute(runFrom = runFrom, command = this)
}