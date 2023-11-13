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
import dev.buijs.klutter.kore.common.verifyExists
import java.io.File
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
    override fun klutterTask() =
        project.rootProject.projectDir.toGetKradleTask()
}

fun File.toGetKradleTask() = object: KlutterTask {
    val dotKradle = resolve(".kradle")
    val dotKradleJar = dotKradle.resolve("kradle-wrapper.jar")

    override fun run() {
        dotKradle.mkdir()
        if(dotKradleJar.exists())
            dotKradleJar.delete()

        kradleWrapperJar.copyToFolder(
            sourceFileName = "kradle-wrapper.jar",
            targetFolder = dotKradle
        )

        kradlew.copyToFolder(
            sourceFileName =  "kradlew",
            targetFolder =  this@toGetKradleTask
        ) {
            it.setExecutable(true)
        }

        kradlewBat.copyToFolder(
            sourceFileName = "kradlew.bat",
            targetFolder = this@toGetKradleTask
        ) {
            it.setExecutable(true)
        }
    }

}

private fun resourceFileOrThrow(name: String): InputStream =
    KlutterGradlePlugin::class.java.getResourceAsStream("/$name")
        ?: throw KlutterException("Unable to find $name")

private fun InputStream.copyToFolder(
    sourceFileName: String,
    targetFolder: File,
    postProcesFile: (File) -> Unit = {}
) {
    val target = targetFolder.resolve(sourceFileName)

    if(target.exists())
        target.delete()

    use { input ->
        target.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    target.verifyExists().let(postProcesFile)
}