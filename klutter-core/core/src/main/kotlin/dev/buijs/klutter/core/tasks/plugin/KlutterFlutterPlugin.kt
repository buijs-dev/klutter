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
import dev.buijs.klutter.core.DartKotlinMap
import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.MethodCallDefinition
import dev.buijs.klutter.core.annotations.processor.KlutterAdapteeScanner
import dev.buijs.klutter.core.annotations.processor.KlutterResponseProcessor
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
             * Path to the README.md file which will be copied to the Flutter plugin folder.
             */
            readmePath: File,

            /**
             * Path to the LICENSE file which will be copied to the Flutter plugin folder.
             */
            licensePath: File,

            /**
             * Path to the CHANGELOG.md file which will be copied to the Flutter plugin folder.
             */
            changelogPath: File,

            /**
             * [FlutterLibraryConfig] containing all the metadata from which to create the Flutter plugin.
             */
            libraryConfig: FlutterLibraryConfig,

            /**
             * [DependencyVersions] containing all iOS and Android dependency versions.
             */
            versions: DependencyVersions,
        ): KlutterFlutterPlugin {

            val pubspec = outputPath.resolve("pubspec.yaml").also { file ->
                FlutterPubspecGenerator(path = file, config = libraryConfig).generate()
            }

            val readme = outputPath.resolve("README.md").also { file ->
                if(!readmePath.exists()) {
                    throw KlutterCodeGenerationException("README.md does not exist in: ${readmePath.absolutePath}")
                }

                if(file.exists()) {
                    file.delete()
                }

                file.createNewFile()
                file.writeText(readmePath.readText())
            }

            val license = outputPath.resolve("LICENSE").also { file ->
                if(!licensePath.exists()) {
                    throw KlutterCodeGenerationException("LICENSE does not exist in: ${licensePath.absolutePath}")
                }

                if(file.exists()) {
                    file.delete()
                }

                file.createNewFile()
                file.writeText(licensePath.readText())
            }

            val changelog = outputPath.resolve("CHANGELOG.md").also { file ->
                if(!changelogPath.exists()) {
                    throw KlutterCodeGenerationException("CHANGELOG.md does not exist in: ${changelogPath.absolutePath}")
                }

                if(file.exists()) {
                    file.delete()
                }

                file.createNewFile()
                file.writeText(changelogPath.readText())
            }

            val libPath = outputPath.resolve("lib").also { folder ->
                if(!folder.exists()) folder.mkdirs()
            }

            // Folder platform/src/commonMain to scan for Klutter annotations
            val source = platformPath.resolve("src/commonMain")

            val methods = KlutterAdapteeScanner(source, context).scan().map {
                //MCD stores the Kotlin type so need to convert it dart type for generating the proper adapter in Dart.
                MethodCallDefinition(
                    getter = it.getter,
                    import = it.import,
                    call = it.call,
                    async = it.async,
                    returns = DartKotlinMap.toMapOrNull(it.returns)?.dartType ?: it.returns,
                )
            }

            val messages = KlutterResponseProcessor(source, context).process()

            val library = libPath.resolve("${libraryConfig.libraryName}.dart").also { file ->
                if(file.exists()) {
                    file.delete()
                }

                file.createNewFile()

                FlutterLibraryGenerator(
                    path = file,
                    methodChannelName = libraryConfig.libraryName,
                    pluginClassName = libraryConfig.pluginClassName,
                    methods = methods,
                    messages = messages,
                )
            }

            val iosPath = outputPath.resolve("ios").also { folder ->

                if(folder.exists()) {
                    folder.deleteRecursively()
                }

                folder.mkdirs()
            }

            val assets = iosPath.resolve("Assets").also {
                it.mkdir()
            }

            val classes = iosPath.resolve("Classes").also {
                it.mkdir()
            }

            val iosPodspec = iosPath.resolve("${libraryConfig.libraryName}.podspec").also { file ->
                file.createNewFile()
                FlutterPodspecGenerator(
                    path = file,
                    config = libraryConfig,
                    iosVersion = versions.iosVersion,
                ).generate()
            }

            val pluginFileH = classes.resolve("${libraryConfig.pluginClassName}.h").also { file ->
                file.createNewFile()
                IosPluginHGenerator(path = file, pluginClassName = libraryConfig.pluginClassName).generate()
            }

            val pluginFileM = classes.resolve("${libraryConfig.pluginClassName}.m").also { file ->
                file.createNewFile()
                IosPluginMGenerator(
                    path = file,
                    pluginClassName = libraryConfig.pluginClassName,
                    libraryName = libraryConfig.libraryName,
                ).generate()
            }

            val swiftPlugin = classes.resolve("Swift${libraryConfig.pluginClassName}.swift").also { file ->
                file.createNewFile()
                IosPluginSwiftGenerator(
                    path = file,
                    pluginClassName = libraryConfig.pluginClassName,
                    methodChannelName = libraryConfig.libraryName,
                    methods = methods,
                ).generate()
            }

            val androidPath = outputPath.resolve("android").also { folder ->

                if(folder.exists()) {
                    folder.deleteRecursively()
                }

                folder.mkdirs()
            }

            val settingsGradle = androidPath.resolve("settings.gradle.kts").also { file ->
                file.createNewFile()
                AndroidSettingsGradleGenerator(path = file).generate()
            }

            val buildGradle = androidPath.resolve("build.gradle").also { file ->
                file.createNewFile()
                AndroidBuildGradleGenerator(
                    path = file,
                    config = libraryConfig,
                    versions = versions,
                ).generate()
            }

            val sourcePath = androidPath.resolve("src/main").also { folder ->
                folder.mkdirs()
            }

            val manifest = sourcePath.resolve("AndroidManifest.xml").also { file ->
                file.createNewFile()
                AndroidManifestGenerator(
                    path = file,
                    libraryName = libraryConfig.libraryName,
                    developerOrganisation = libraryConfig.developerOrganisation
                ).generate()
            }

            val pluginPath = sourcePath
                .resolve("kotlin")
                .resolve(libraryConfig.developerOrganisation.replace(".", "/"))
                .resolve(libraryConfig.libraryName)
                .also { folder -> folder.mkdirs() }

            val plugin = pluginPath.resolve("${libraryConfig.pluginClassName}.kt").also { file ->
                file.createNewFile()
                AndroidPluginGenerator(
                    path = file,
                    methodChannelName = libraryConfig.libraryName,
                    libraryConfig = libraryConfig,
                    methods = methods,
                ).generate()
            }

            return KlutterFlutterPlugin(
                readme = readme,
                license = license,
                changelog = changelog,
                pubspecYaml = pubspec,
                lib = LibFolder(library),
                ios = IosFolder(
                    podspec = iosPodspec,
                    assets = assets,
                    classes = IosClasses(
                        pluginFileH = pluginFileH,
                        pluginFileM = pluginFileM,
                        swiftPlugin = swiftPlugin,
                    ),
                ),
                android = AndroidFolder(
                    buildGradle = buildGradle,
                    settingsGradle = settingsGradle,
                    srcMain = AndroidSource(
                        manifest = manifest,
                        plugin = plugin,
                    ),
                ),
            )
        }
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
data class DependencyVersions(
    val androidGradleVersion: String,
    val kotlinVersion: String,
    val kotlinxVersion: String,
    val klutterVersion: String,
    val iosVersion: String,
    val compileSdkVersion: Int,
    val minSdkVersion: Int,
)