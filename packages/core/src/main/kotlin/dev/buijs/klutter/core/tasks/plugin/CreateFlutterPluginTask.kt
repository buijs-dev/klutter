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
package dev.buijs.klutter.core.tasks.plugin

import com.intellij.openapi.project.Project
import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.tasks.adapter.platform.PlatformBuildGradleScanner
import java.io.File
import java.nio.file.Path

private const val buildGradle = "build.gradle.kts"
private const val klutterVersion = "2022-alpha-1"
private const val androidGradleVersion = "7.0.4"
private const val kotlinVersion = "1.6.10"
private const val androidKotlinxVersion = "1.3.2"

/**
 * Task to create a Flutter plugin from a KMP platform module.
 *
 * @author Gillian Buijs
 */
class CreateFlutterPluginTask(
    private val context: Project,
    private val libraryName: String,
    private val libraryDescription: String,
    private val libraryVersion: String,
    private val homepageLink: String,
    private val organisation: String,
    projectFolder: String,
    outputLocation: String,
    flutterDocumentation: FlutterLibraryDocumentation?
)
    : KlutterTask
{

    private val folder = Path.of("$outputLocation/$libraryName").toFile()
    private val copyFrom = Path.of(projectFolder).toAbsolutePath().toFile()

    private val scanner = PlatformBuildGradleScanner(copyFrom.resolve(buildGradle))

    // Extract Android Config from build.gradle.kts file.
    private val androidConfig = scanner.androidConfig()

    // Extract IOS Version from build.gradle.kts file.
    private val iosVersion = scanner.iosVersion()

    private val readmePath = flutterDocumentation?.readme?.absolutePath
        ?: copyFrom.resolve("flutter/README.md").absolutePath

    private val changelogPath = flutterDocumentation?.changelog?.absolutePath
        ?: copyFrom.resolve("flutter/CHANGELOG.md").absolutePath

    private val licensePath = flutterDocumentation?.license?.absolutePath
        ?: copyFrom.resolve("flutter/LICENSE").absolutePath

    override fun run() {

        // Copy the Platform Module to the generated Flutter project.
        copyFrom.resolve("platform").let { module ->
            if(!module.exists()) {
                throw KlutterConfigException("Folder not found: '${module.absolutePath}'. " +
                        "Make sure the configured 'projectFolder' " +
                        "contains a Kotlin Multiplatform shared module named 'platform'.")
            }

            val platform = folder.resolve("platform").also { it.mkdirs() }
            val filesToCopy = listOf("src", buildGradle, "platform.podspec")
            module.list { location, name ->

                if(filesToCopy.contains(name)) {
                    val fqn = location.resolve(name)
                    fqn.copyRecursively(platform.resolve(name))
                }

                true

            }

        }

        // Copy root build.gradle.kts.
        copyFrom.resolve(buildGradle).copyTo(folder.resolve(buildGradle))

        KlutterFlutterPlugin.generate(
            context = context,
            platformPath = folder.resolve("platform"),
            outputPath = folder,
            libraryDocs = FlutterLibraryDocumentation(
                readme = File(readmePath),
                changelog = File(changelogPath),
                license = File(licensePath),
            ),
            libraryConfig = FlutterLibraryConfig(
                libraryName = libraryName,
                libraryVersion = libraryVersion,
                libraryHomepage = homepageLink,
                libraryDescription = libraryDescription,
                developerOrganisation = organisation,
                pluginClassName = libraryName.toCamelCase(),
            ),
            versions = DependencyVersions(
                androidGradleVersion = androidGradleVersion,
                kotlinVersion = kotlinVersion,
                kotlinxVersion = androidKotlinxVersion,
                klutterVersion = klutterVersion,
                compileSdkVersion = androidConfig.compileSdk,
                minSdkVersion = androidConfig.minSdk,
                iosVersion = iosVersion
            )
        )

    }

}

internal fun String.toCamelCase() = split("_")
    .map { it.replaceFirstChar { char -> char.uppercase()} }
    .joinToString("") { it }