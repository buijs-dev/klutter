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
package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.gradle.KlutterGradlePlugin
import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.KlutterTask
import java.io.InputStream

internal val kradleWrapperJar: InputStream
    get() = resourceFileOrThrow("kradle-wrapper.jar")

internal val kradlew: InputStream
    get() = resourceFileOrThrow("kradlew")

internal val kradlewBat: InputStream
    get() = resourceFileOrThrow("kradlew.bat")

/**
 * Download Kommand CLI Tool.
 */
internal open class GetKradleTask: AbstractTask() {
    override fun klutterTask() = object: KlutterTask {
        val root = project.rootProject.projectDir
        val rootKradlew = root.resolve("kradlew")
        val rootKradlewBat = root.resolve("kradlew.bat")
        val dotKradle = root.resolve(".kradle")
        val dotKradleJar = dotKradle.resolve("kradle-wrapper.jar")

        override fun run() {

            // Create .kradle folder
            dotKradle.mkdir()

            // Copy jar to .kradle folder
            if(dotKradleJar.exists()) dotKradleJar.delete()
            kradleWrapperJar.use { input ->
                dotKradleJar.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Copy kradlew to root and setExecutable
            if(rootKradlew.exists())
                rootKradlew.delete()

            kradlew.use { input ->
                rootKradlew.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if(rootKradlew.exists()) {
                rootKradlew.setExecutable(true)
            } else {
                throw KlutterException("Failed to copy kradlew File to $root")
            }

            // Copy kradlew.bat to root and setExecutable
            if(rootKradlewBat.exists())
                rootKradlewBat.delete()

            kradlewBat.use { input ->
                rootKradlew.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if(rootKradlewBat.exists()) {
                rootKradlewBat.setExecutable(true)
            } else {
                throw KlutterException("Failed to copy kradlew.bat File to $root")
            }

        }
    }
}

private fun resourceFileOrThrow(name: String): InputStream =
    KlutterGradlePlugin::class.java.getResourceAsStream("/$name")
        ?: throw KlutterException("Unable to find $name")