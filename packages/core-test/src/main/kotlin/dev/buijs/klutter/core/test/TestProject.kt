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

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories

/**
 * @author Gillian Buijs
 */
internal data class TestProject(
    val projectDir: Path = Files.createTempDirectory(""),
    val buildSrc: File = projectDir.resolve("buildSrc")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdir() },
    val iosDir: File = projectDir.resolve("ios")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdir() },
    val iosRunnerDir: File = iosDir.resolve("Runner")
        .absoluteFile
        .also { it.mkdir() },
    val iosInfoPlist: File = iosRunnerDir.resolve("Info.plist")
        .absoluteFile
        .also { it.createNewFile() },
    val iosAppDelegate: File = iosRunnerDir.resolve("AppDelegate.swift")
        .absoluteFile
        .also { it.createNewFile() },
    val iosFlutterDir: File = iosDir.resolve("Flutter")
        .absoluteFile
        .also { it.mkdir() },
    val appFrameworkInfoPlist: File = iosFlutterDir.resolve("AppFrameworkInfo.plist")
        .absoluteFile
        .also { it.createNewFile() },
    val androidDir: File = projectDir.resolve("android")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs()},
    val androidAppDir: File = projectDir.resolve("android/app")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs() }
        .also { projectDir.resolve("android/app/src/main").createDirectories() },
    val androidAppManifest: File = projectDir.resolve("android/app/src/main/AndroidManifest.xml")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile()},
    val platformDir: File = projectDir.resolve("platform")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs()},
    val platformSourceDir: File = projectDir.resolve("platform/src/commonMain")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs()},
    val flutterDir: File = projectDir.resolve("lib")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs() },
    val flutterMainFile: File = flutterDir.resolve("main.dart")
        .toPath()
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
    val flutterPubspec: File = projectDir.resolve("plugin_pubspec")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
    val buildGradle: File = projectDir.resolve("build.gradle.kts")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
    val settingsGradle: File = projectDir.resolve("settings.gradle.kts")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
)