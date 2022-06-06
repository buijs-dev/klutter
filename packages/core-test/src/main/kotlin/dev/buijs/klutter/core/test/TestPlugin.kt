@file:JvmName("KlutterTest")
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

package dev.buijs.klutter.core.test

import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files

private val resources = TestResource()

data class PluginProject (
    val pluginName: String = "super_awesome",
    val root: File = Files.createTempDirectory("").toFile(),
    val ios: File = root.createFolder("ios"),
    val iosClasses: File = ios.createFolder("Classes"),
    val iosPodspec: File = ios.createFile("super_awesome.podspec"),
    val iosPodfile: File = ios.createFile("Podfile"),
    val iosRunner: File = ios.createFolder("Runner"),
    val iosAppDelegate: File = iosRunner.createFile("AppDelegate.swift"),
    val android: File = root.createFolder("android"),
    val androidMain: File = root.createFolder("android/src/main").also {
        it.createFolder("kotlin/foo/bar/super_awesome")
    },
    val androidManifest: File = androidMain.createFile("AndroidManifest.xml"),
    val platform: File = root.createFolder("platform"),
    val platformBuildGradle: File = platform.createFile("build.gradle.kts"),
    val platformSource: File = root.createFolder("${platform.path}/src/commonMain"),
    val platformSourceClass: File = platformSource.createFile("FakeClass.kt"),
    val platformPodspec: File = platform.createFile("super_awesome.podspec"),
    val flutter: File = root.createFolder("lib"),
    val flutterMainClass: File = flutter.createFile("${pluginName}.dart"),
    val rootBuildGradle: File = root.createFile("build.gradle.kts"),
    val rootSettingsGradle: File = root.createFile("settings.gradle.kts"),
    val pubspecYaml: File = root.createFile("pubspec.yaml")
) {

    fun verify(
        message: String = "Test Failure",
        assertion: (project: PluginProject, resources: TestResource) -> Boolean,
    ) {
        val passed = assertion.invoke(this, TestResource())
        assert(passed) { message }
    }

    fun test(
        projectDir: File = root,
        vararg args: String,
    ) {
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments(args.toMutableList().also { list -> list.add("--stacktrace") })
            .build()
    }

    private fun hasChild(
        file: File,
        compareToResource: String = "",
        compareMode: CompareMode = CompareMode.IDENTICAL,
    ): Boolean {

        if(!file.exists()) {
            assert(false) { "File does not exist: $file" }
        }

        if(compareToResource != "") {
            return compare(
                file.readText(),
                resources.load(compareToResource),
                compareMode,
            )
        }

        return true
    }

    fun hasChild(
        path: String = "",
        filename: String = "",
        compareToResource: String = "",
        compareMode: CompareMode = CompareMode.IDENTICAL,
    ): Boolean {

        if(path != "" && !File(path).exists()) {
            return false
        }

        return hasChild(
            File("$path/$filename").normalize(),
            compareToResource,
            compareMode,
        )
    }

    private fun compare(
        actual: String,
        expected: String,
        compareMode: CompareMode,
    ) = when(compareMode) {

        CompareMode.IDENTICAL -> {
           actual == expected
        }

        CompareMode.IGNORE_SPACES -> {
            val actualNoSpaces = actual.filter { !it.isWhitespace() }
            val expectedNoSpaces = expected.filter { !it.isWhitespace() }
            if(actualNoSpaces == expectedNoSpaces) {
                true
            } else {
                println("======== Comparison FAILED (Ignoring Spaces) ========")
                println("")
                println("======== EXPECTED ========")
                println(expected)
                println("")
                println("======== ACTUAL ========")
                println(actual)
                println("")
                println("======== EXPECTED (Ignoring Spaces) ========")
                println(expectedNoSpaces)
                println("")
                println("======== ACTUAL (Ignoring Spaces) ========")
                println(actualNoSpaces)
                println("")
                false
            }

        }

    }
}

fun plugin(
    setup: (project: PluginProject, resources: TestResource) -> PluginProject = { _, _ -> PluginProject() },
): PluginProject {
    return PluginProject().also { setup.invoke(it, TestResource()) }
}

private fun File.createFile(path: String) = resolve(path).absoluteFile.also { file ->
    file.normalize().let { if(!it.exists()) it.createNewFile() }
}

private fun File.createFolder(path: String) = resolve(path).absoluteFile.also { file ->
    file.normalize().let { if(!it.exists()) it.mkdirs() }
}

enum class CompareMode {
    IDENTICAL,
    IGNORE_SPACES,
}