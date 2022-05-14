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
import java.io.File

/**
 * @author Gillian Buijs
 */
data class KlutterFlutterPlugin(

    /**
     * The plugin README.md file displayed on pub.dev.
     */
    val readme: File,

    /**
     * The plugin LICENSE.md file displayed on pub.dev.
     */
    val license: File,

    /**
     * The plugin CHANGELOG.md file displayed on pub.dev.
     */
    val changelog: File,

    /**
     * The plugin pubspec.yaml file used to publish KMP module as a Flutter library.
     */
    val pubspecYaml: File,

    /**
     * The root/lib folder containing the .dart implementation files.
     */
    val lib: LibFolder,

    /**
     * The root/ios folder.
     */
    val ios: IosFolder,

    /**
     * The root/android folder.
     */
    val android: AndroidFolder

) {
    companion object {
        fun generate(
            /**
             * Reference to the Kotlin project which is
             * used to scan the Kotlin Multiplatform
             * commonMain folder for @Klutter annotations.
             */
            context: Project,

            /**
             * Path to the Kotlin Multiplatform module.
             */
            platformPath: File,

            /**
             * Path to the output folder where to create the Flutter plugin.
             */
            outputPath: File,

            /**
             * Path to README.md, CHANGELOG.md and LICENSE files.
             */
            libraryDocs: FlutterLibraryDocumentation,

            /**
             * [FlutterLibraryConfig] containing all the metadata from which to create the Flutter plugin.
             */
            libraryConfig: FlutterLibraryConfig,

            /**
             * [DependencyVersions] containing all iOS and Android dependency versions.
             */
            versions: DependencyVersions,
        ) = generateFlutterPluginFromKmpSource(
            context = context,
            platformPath = platformPath,
            outputPath = outputPath,
            libraryDocs = libraryDocs,
            libraryConfig = libraryConfig,
            versions = versions,
        )
    }
}

/**
 * @author Gillian Buijs
 */
data class LibFolder(

    /**
     * The top level .dart file exposing the plugin interface.
     */
    val library: File,
)

/**
 * @author Gillian Buijs
 */
data class IosFolder(

    /**
     * The Flutter plugin root/ios .podspec file.
     */
    val podspec: File,

    /**
     * The Flutter plugin root/ios/Assets folder.
     */
    val assets: File,

    /**
     * The Flutter plugin root/ios/Classes folder
     * containing the IOS boilerplate for communicating with the KMP library.
     */
    val classes: IosClasses,

)

/**
 * @author Gillian Buijs
 */
data class IosClasses(

    /**
     * The root/ios/Classes/xxxPlugin.h file exposing a Plugin interface.
     */
    val pluginFileH: File,

    /**
     * The root/ios/Classes/xxxPlugin.m file which registers the actual IOS plugin.
     */
    val pluginFileM: File,

    /**
     * The root/ios/classes/SwiftxxxPlugin.swift file containing the generated code
     * for handling method channel calls.
     */
    val swiftPlugin: File,
)

/**
 * @author Gillian Buijs
 */
data class AndroidFolder(

    /**
     * The root/android/src/main folder containing the
     * Android boilerplate for communicating with the KMP library.
     */
    val srcMain: AndroidSource,

    /**
     * The root/android/build.gradle file for building an Android artifact.
     */
    val buildGradle: File,

    /**
     * The root/android/settings.gradle file.
     */
    val settingsGradle: File,

)

/**
 * @author Gillian Buijs
 */
data class AndroidSource(

    /**
     * The root/android/src/main/AndroidManifest file which sets the plugin package name.
     */
    val manifest: File,

    /**
     * The root/android/src/main/kotlin/x/y/z/xxxPlugin.kt file containing the Kotlin plugin interface.
     */
    val plugin: File,
)

/**
 * @author Gillian Buijs
 */
data class FlutterLibraryConfig(
    val libraryName: String,
    val libraryDescription: String,
    val libraryVersion: String,
    val libraryHomepage: String,
    val developerOrganisation: String,
    val pluginClassName: String,
)

/**
 * @author Gillian Buijs
 */
data class FlutterLibraryDocumentation(

    /**
     * Path to the README.md file which will be copied to the Flutter plugin folder.
     */
    val readme: File,

    /**
     * Path to the CHANGELOG.md file which will be copied to the Flutter plugin folder.
     */
    val changelog: File,

    /**
     * Path to the LICENSE file which will be copied to the Flutter plugin folder.
     */
    val license: File,
)

/**
 * @author Gillian Buijs
 */
data class DependencyVersions(
    val androidGradleVersion: String,
    val kotlinVersion: String,
    val kotlinxVersion: String,
    val klutterVersion: String,
    val iosVersion: String,
    val compileSdkVersion: Int,
    val minSdkVersion: Int,
)