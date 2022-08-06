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
package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.shared.execute
import dev.buijs.klutter.gradle.KlutterGradleTask
import org.jetbrains.kotlin.incremental.cleanDirectoryContents

/**
 * Task to start the appium server.
 */
internal open class AppiumServerStart : KlutterGradleTask() {

    override fun describe() {
        val root = project().root.folder
        val properties = root.resolve("local.properties").also {
            if(!it.exists()) {
                throw KlutterException("Missing local.properties in root folder.")
            }
        }

        val androidSdk = properties.readLines().mapNotNull { line ->
            if (line.trim().startsWith("sdk.dir")) {
                line.substringAfter("sdk.dir=")
            } else {
                null
            }
        }.first()

        val pathToTestFolder = project.pathToTestFolder().also {
            if(!it.exists()) throw KlutterException("Folder not found $it")
        }

        val pathToCapabilities = pathToTestFolder.resolve("src/test/resources/capabilities.json").also {
            if(!it.exists()) {
                it.createNewFile()
            }
        }

        val pathToLogFolder = pathToTestFolder.resolve("build/test-logging").also {
            if(!it.exists()) {
                it.mkdirs()
            }

            it.cleanDirectoryContents()
        }

        """export ANDROID_HOME="$androidSdk"
           export ANDROID_SDK_ROOT="$androidSdk"
           appium --default-capabilities ${pathToCapabilities.absolutePath} 
           --log-level debug 
           --log ${pathToLogFolder.resolve("appium-server.log")}""".execute(project().root.folder)
    }



}

