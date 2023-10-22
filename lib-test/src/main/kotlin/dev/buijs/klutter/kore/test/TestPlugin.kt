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

package dev.buijs.klutter.kore.test

import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files

data class TestPlugin (
    val pluginName: String = "super_awesome",
    val pluginClassName: String = "SuperAwesomePlugin",
    val pluginPackageName: String = "com.example.super_awesome",
    val root: File = Files.createTempDirectory("").toFile(),
    val libFolder: File = root.createFolder("lib"),
    val libFile: File = libFolder.createFile("${pluginName}.dart"),

    val platform: File = root.resolve("platform"),
    val platformCommonMain: File = platform.createFolder("src/commonMain"),
    val platformSourceFile: File = platformCommonMain.createFile("FakeClass.kt"),
    val platformPodSpec: File = platform.createFile("${pluginName}.podspec"),

    val ios: File = root.createFolder("ios"),
    val iosClasses: File = ios.createFolder("Classes"),
    val iosPodspec: File = ios.createFile("$pluginName.podspec"),
    val podfile: File = ios.createFile("Podfile"),
    val runnerFolder: File = ios.createFolder("Runner"),
    val appDelegate: File = runnerFolder.createFile("AppDelegate.swift"),

    val android: File = root.createFolder("android"),
    val androidSrcMain: File = android.createFolder("src/main"),
    val manifest: File = androidSrcMain.createFile("AndroidManifest.xml"),
    val pathToPluginSource: File = androidSrcMain.createFolder("kotlin/foo/bar/$pluginName/"),
    val pathToPlugin: File = pathToPluginSource.createFile("$pluginClassName.kt"),

    val platformBuildGradle: File = platform.createFile("build.gradle.kts"),
    val rootSettingsGradle: File = root.createFile("settings.gradle.kts"),
    val pubspecYaml: File = root.createFile("pubspec.yaml").also {
        it.writeText(TestResource().load("plugin_pubspec"))
    },
) {
    fun test(
        projectDir: File,
        vararg args: String,
    ) {
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withTestKitDir(projectDir)
            .withPluginClasspath()
            .withArguments(args.toMutableList().also { list -> list.add("--stacktrace") })
            .build()
    }
}

private fun File.createFile(path: String) = resolve(path).absoluteFile.also { file ->
    file.normalize().let { if(!it.exists()) it.createNewFile() }
}

private fun File.createFolder(path: String) = resolve(path).absoluteFile.also { file ->
    file.normalize().let { if(!it.exists()) it.mkdirs() }
}