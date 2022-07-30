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

package dev.buijs.klutter.plugins.gradle.tasks

import dev.buijs.klutter.plugins.gradle.KlutterGradleTask
import dev.buijs.klutter.plugins.gradle.klutterExtension
import dev.buijs.klutter.ui.tasks.AndroidArtifactBuildTask
import dev.buijs.klutter.ui.tasks.IosArtifactBuildTask
import org.gradle.api.Project
import java.io.File

/**
 * Task to build debug .apk for Android and Runner.app for IOS.
 */
internal open class BuildAndroidAndIosWithFlutter : KlutterGradleTask() {
    override fun describe() {
        project.pathToTestFolder().let { pathToTestFolder ->
            project.buildAndroid(pathToTestFolder)
            project.buildIos(pathToTestFolder)
        }
    }
}

/**
 * Task to build debug .apk Android artifact.
 */
internal open class BuildAndroidWithFlutter : KlutterGradleTask() {
    override fun describe() = project.buildAndroid(project.pathToTestFolder().resolve("src/test/resources"))
}

/**
 * Task to build Runner.app IOS artifact.
 */
internal open class BuildIosWithFlutter : KlutterGradleTask() {
    override fun describe() = project.buildIos(project.pathToTestFolder().resolve("src/test/resources"))
}

private fun Project.buildIos(pathToTestFolder: File) = IosArtifactBuildTask(
    pathToFlutterApp = project.rootProject.rootDir.resolve("app/frontend"),
    pathToOutput = pathToTestFolder,
).run()

private fun Project.buildAndroid(pathToTestFolder: File) = AndroidArtifactBuildTask(
    pathToFlutterApp = project.rootProject.rootDir.resolve("app/frontend"),
    pathToOutput = pathToTestFolder,
).run()

internal fun Project.pathToTestFolder(): File = project.klutterExtension()
    .application
    ?.uiTestFolder
    ?:project.rootProject.rootDir.resolve("lib-test")