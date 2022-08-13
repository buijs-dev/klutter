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
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

private const val klutterPubVersion = "0.2.0"

/**
 * Task to generate a klutter plugin project.
 */
class GenerateKlutterPluginProjectTask(
    /**
     * Path to the folder where to create the new project.
     */
    private val pathToRoot: String,

    /**
     * Name of the application.
     */
    private val appName: String,

    /**
     * Name of the application organisation.
     */
    private val groupName: String,

) : KlutterTask {

    override fun run() {
       File(pathToRoot).also { root ->
           root.verifyExists()
           root.createApp()
       }
    }

    private fun File.createApp() {
        "flutter create $appName --org $groupName --template=plugin --platforms=android,ios".execute(this)

        val pluginFolder = resolve(appName)
        val exampleFolder = resolve(appName).resolve("example")
        val pubspecs = listOf(
            pluginFolder.resolve("pubspec.yaml"),
            exampleFolder.resolve("pubspec.yaml"),
        )

        for(pubspec in pubspecs) {
            val lines = pubspec.readLines()
            val updated = mutableListOf<String>()
            for(line in lines) {
                updated.add(line)
                if (line.trim().startsWith("dependencies:")) {
                    updated.add("  klutter: ^$klutterPubVersion")
                }
            }
            pubspec.writeText(updated.joinToString("\n"))
        }

        "flutter pub get".execute(pluginFolder)
        "flutter pub get".execute(exampleFolder)
        "flutter pub run klutter:producer init".execute(pluginFolder)
        "flutter pub run klutter:consumer init".execute(exampleFolder)
        "flutter pub run klutter:consumer add=$appName".execute(exampleFolder)

        resolve("gradlew.bat").let { gradlew ->
            gradlew.setExecutable(true)
            gradlew.setReadable(true)
            gradlew.setWritable(true)
        }

        resolve("gradlew").let { gradlew ->
            gradlew.setExecutable(true)
            gradlew.setReadable(true)
            gradlew.setWritable(true)
        }
    }

}