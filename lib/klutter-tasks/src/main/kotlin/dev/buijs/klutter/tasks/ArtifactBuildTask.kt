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

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.KlutterTask
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 *
 */
sealed class ArtifactBuildTask(

    /**
     * Path to the Flutter frontend folder.
     */
    private val pathToFlutterApp: File,

    /**
     * Path to folder to store the build artifact.
     */
    private val pathToOutput: File,

    ) : KlutterTask {

    internal fun pathToFlutterApp(): File {
        if (!pathToFlutterApp.exists()) {
            throw KlutterException("Missing directory: $pathToFlutterApp.")
        }
        return pathToFlutterApp
    }

    internal fun pathToOutputFolder(): File {
        if (!pathToOutput.exists()) {
            throw KlutterException("Missing output directory: $pathToOutput.")
        }
        return pathToOutput
    }
}

/**
 *
 */
class AndroidArtifactBuildTask(
    pathToFlutterApp: File,
    pathToOutput: File,
): ArtifactBuildTask(pathToFlutterApp, pathToOutput) {

    override fun run() {
        // Build the artifact using Flutter.
        """flutter build apk --debug""".execute(pathToFlutterApp())

        // Check if artifact exists and fail if not.
        val artifact = pathToFlutterApp().resolve("build/app/outputs/flutter-apk/app-debug.apk").also {
            if(!it.exists()) {
                throw KlutterException("Failed to build Android artifact.")
            }
        }

        // Copy the artifact to the destination.
        artifact.copyTo(pathToOutputFolder())

    }

}

/**
 *
 */
class IosArtifactBuildTask(
    pathToFlutterApp: File,
    pathToOutput: File,
): ArtifactBuildTask(pathToFlutterApp, pathToOutput) {

    override fun run() {
        // Build the artifact using Flutter.
        """flutter build ios --no-codesign --debug""".execute(pathToFlutterApp())

        // Check if artifact exists and fail if not.
        val artifact = pathToFlutterApp().resolve("build/ios/iphonesimulator/Runner.app").also {
            if(!it.exists()) {
                throw KlutterException("Failed to build IOS artifact.")
            }
        }

        // Zip the Runner.app artifact.
        val artifactZip = File.createTempFile("out", ".zip").also { zip ->
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zip))).use { zos ->
                artifact.walkTopDown().forEach { file ->

                    val zipFileName = file.absolutePath
                        .removePrefix(artifact.absolutePath)
                        .removePrefix("/")

                    zos.putNextEntry(
                        ZipEntry( "$zipFileName${(if (file.isDirectory) "/" else "" )}")
                    )

                    if (file.isFile) {
                        file.inputStream().copyTo(zos)
                    }

                }
            }
        }

        // Copy the artifact to the destination.
        artifactZip.copyTo(pathToOutputFolder())

    }

}