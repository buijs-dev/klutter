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
import dev.buijs.klutter.kore.project.Project
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Task to build a Klutter plugin project.
 *
 * Executes the following steps:
 * - clean platform module
 * - build platform module
 * - create XCFramework
 * - klutterCopyAarFile
 * - klutterCopyFramework
 *
 */
class BuildKlutterPluginProjectTask(
    private val project: Project,
    private val executor: CliExecutor = CliExecutor(),
) : KlutterTask {

    override fun run() {
        executor.execute(
            command = """./gradlew clean build assemblePlatformReleaseXCFramework -p platform""",
            runFrom = project.root.folder,
        )

        executor.execute(
            command = """./gradlew klutterCopyAarFile""",
            runFrom = project.root.folder,
            timeout = 30
        )

        executor.execute(
            command = """./gradlew klutterCopyFramework""",
            runFrom = project.root.folder,
            timeout = 30
        )

    }

}

/**
 * Task to build debug .apk for Android and Runner.app for IOS.
 */
class BuildAndroidAndIosWithFlutterTask(
    private val pathToFlutterApp: File,
    private val pathToTestFolder: File,
) : KlutterTask {
    override fun run() {
        buildAndroid(pathToTestFolder, pathToFlutterApp)
        buildIos(pathToTestFolder, pathToFlutterApp)
    }
}

/**
 * Task to build debug .apk Android artifact.
 */
class BuildAndroidWithFlutterTask(
    private val pathToTestFolder: File,
    private val pathToFlutterApp: File,
) : KlutterTask {
    override fun run() = buildAndroid(pathToTestFolder, pathToFlutterApp)
}

/**
 * Task to build Runner.app IOS artifact.
 */
class BuildIosWithFlutterTask(
    private val pathToTestFolder: File,
    private val pathToFlutterApp: File,
) : KlutterTask {
    override fun run() = buildIos(pathToTestFolder, pathToFlutterApp)
}

private fun buildIos(
    pathToTestFolder: File,
    pathToToFlutterApp: File,
) = IosArtifactBuildTask(
    pathToFlutterApp = pathToToFlutterApp,
    pathToOutput = pathToTestFolder.resolve("src/test/resources"),
).run()

private fun buildAndroid(
    pathToTestFolder: File,
    pathToToFlutterApp: File,
) = AndroidArtifactBuildTask(
    pathToFlutterApp = pathToToFlutterApp,
    pathToOutput = pathToTestFolder.resolve("src/test/resources"),
).run()

/**
 *
 */
private sealed class ArtifactBuildTask(

    /**
     * Path to the Flutter frontend folder.
     */
    private val pathToFlutterApp: File,

    /**
     * Path to folder to store the build artifact.
     */
    private val pathToOutput: File,

    ) : KlutterTask {

    fun pathToFlutterApp(): File {
        if (!pathToFlutterApp.exists()) {
            throw KlutterException("Missing directory: $pathToFlutterApp.")
        }
        return pathToFlutterApp
    }

    fun pathToOutputFolder(): File {
        if (!pathToOutput.exists()) {
            throw KlutterException("Missing output directory: $pathToOutput.")
        }
        return pathToOutput
    }
}

/**
 * Task to build debug app with flutter.
 */
private class AndroidArtifactBuildTask(
    pathToFlutterApp: File,
    pathToOutput: File,
    private val executor: CliExecutor = CliExecutor(),
): ArtifactBuildTask(pathToFlutterApp, pathToOutput) {

    override fun run() {
        // Build the artifact using Flutter.
        executor.execute(
            command = """flutter build apk --debug""",
            runFrom = pathToFlutterApp(),
        )

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
 * Task to build Runner.app with Flutter.
 */
private class IosArtifactBuildTask(
    pathToFlutterApp: File,
    pathToOutput: File,
    private val executor: CliExecutor = CliExecutor(),
): ArtifactBuildTask(pathToFlutterApp, pathToOutput) {

    override fun run() {
        // Build the artifact using Flutter.
        executor.execute(
            command = """flutter build ios --no-codesign --debug""",
            runFrom = pathToFlutterApp(),
        )

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