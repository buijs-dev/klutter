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
import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.KlutterFileGenerator
import dev.buijs.klutter.core.MethodCallDefinition
import dev.buijs.klutter.core.annotations.processor.KlutterAdapteeScanner
import dev.buijs.klutter.core.annotations.processor.KlutterResponseProcessor
import dev.buijs.klutter.core.annotations.processor.ReturnTypeLanguage
import dev.buijs.klutter.core.tasks.plugin.android.AndroidBuildGradleGenerator
import dev.buijs.klutter.core.tasks.plugin.android.AndroidManifestGenerator
import dev.buijs.klutter.core.tasks.plugin.android.AndroidPluginGenerator
import dev.buijs.klutter.core.tasks.plugin.android.AndroidSettingsGradleGenerator
import dev.buijs.klutter.core.tasks.plugin.flutter.FlutterLibraryGenerator
import dev.buijs.klutter.core.tasks.plugin.flutter.FlutterPodspecGenerator
import dev.buijs.klutter.core.tasks.plugin.flutter.FlutterPubspecGenerator
import dev.buijs.klutter.core.tasks.plugin.ios.IosPluginHGenerator
import dev.buijs.klutter.core.tasks.plugin.ios.IosPluginMGenerator
import dev.buijs.klutter.core.tasks.plugin.ios.IosPluginSwiftGenerator
import java.io.File

/**
 * @author Gillian Buijs
 */
fun generateFlutterPluginFromKmpSource(

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
): KlutterFlutterPlugin {

    val pubspec = outputPath.resolve("pubspec.yaml").also { file ->
        FlutterPubspecGenerator(path = file, config = libraryConfig).generate()
    }

    val readme = copyFlutterDoc(
        outputPath = outputPath,
        sourceFile = libraryDocs.readme,
        filename = "README.md",
    )

    val changelog = copyFlutterDoc(
        outputPath = outputPath,
        sourceFile = libraryDocs.changelog,
        filename = "CHANGELOG.md",
    )

    val license = copyFlutterDoc(
        outputPath = outputPath,
        sourceFile = libraryDocs.license,
        filename = "LICENSE",
    )

    val source = platformPath.resolve("src/commonMain")
    val methods = KlutterAdapteeScanner(source, context).scan(language = ReturnTypeLanguage.DART)
    val messages = KlutterResponseProcessor(source, context).process()
    val libPath = outputPath.resolve("lib").create()
    val library =
        libPath.resolve("${libraryConfig.libraryName}.dart").create(clear = true) { file ->
            FlutterLibraryGenerator(
                path = file,
                methodChannelName = libraryConfig.libraryName,
                pluginClassName = libraryConfig.pluginClassName,
                methods = methods,
                messages = messages,
            )
        }

    return KlutterFlutterPlugin(
        readme = readme,
        license = license,
        changelog = changelog,
        pubspecYaml = pubspec,
        lib = LibFolder(library),
        ios = createIosFolder(
            outputPath = outputPath,
            libraryConfig = libraryConfig,
            versions = versions,
            methods = methods,
        ),
        android = createAndroidFolder(
            outputPath = outputPath,
            libraryConfig = libraryConfig,
            versions = versions,
            methods = methods,
        )
    )
}

/**
 * Create the IOS folder structure for the Flutter plugin.
 */
internal fun createIosFolder(
    outputPath: File,
    libraryConfig: FlutterLibraryConfig,
    versions: DependencyVersions,
    methods: List<MethodCallDefinition>,
): IosFolder {

    val iosPath = outputPath.resolve("ios").create(clear = true)
    val assets = iosPath.resolve("Assets").create()
    val classes = iosPath.resolve("Classes").create()

    val iosPodspec = iosPath.resolve("${libraryConfig.libraryName}.podspec")
        .create { file ->
            FlutterPodspecGenerator(
                path = file,
                config = libraryConfig,
                iosVersion = versions.iosVersion,
            )
        }

    val pluginFileH = classes.resolve("${libraryConfig.pluginClassName}.h").create { file ->
        IosPluginHGenerator(path = file, pluginClassName = libraryConfig.pluginClassName)
    }

    val pluginFileM = classes.resolve("${libraryConfig.pluginClassName}.m").create { file ->
        IosPluginMGenerator(
            path = file,
            pluginClassName = libraryConfig.pluginClassName,
            libraryName = libraryConfig.libraryName,
        )
    }

    val swiftPlugin =
        classes.resolve("Swift${libraryConfig.pluginClassName}.swift").create { file ->
            IosPluginSwiftGenerator(
                path = file,
                pluginClassName = libraryConfig.pluginClassName,
                methodChannelName = libraryConfig.libraryName,
                methods = methods,
            )
        }

    return IosFolder(
        podspec = iosPodspec,
        assets = assets,
        classes = IosClasses(
            pluginFileH = pluginFileH,
            pluginFileM = pluginFileM,
            swiftPlugin = swiftPlugin,
        ),
    )
}

/**
 * Create the Android folder structure for the Flutter plugin.
 */
internal fun createAndroidFolder(
    outputPath: File,
    libraryConfig: FlutterLibraryConfig,
    versions: DependencyVersions,
    methods: List<MethodCallDefinition>,
): AndroidFolder {

    val androidPath = outputPath.resolve("android").create(clear = true)

    val settingsGradle = androidPath.resolve("settings.gradle.kts").create { file ->
        AndroidSettingsGradleGenerator(path = file)
    }

    val buildGradle = androidPath.resolve("build.gradle").create { file ->
        AndroidBuildGradleGenerator(
            path = file,
            config = libraryConfig,
            versions = versions,
        )
    }

    val sourcePath = androidPath.resolve("src/main").create()

    val manifest = sourcePath.resolve("AndroidManifest.xml").create { file ->
        AndroidManifestGenerator(
            path = file,
            libraryName = libraryConfig.libraryName,
            developerOrganisation = libraryConfig.developerOrganisation
        )
    }

    val pluginPath = sourcePath
        .resolve("kotlin")
        .resolve(libraryConfig.developerOrganisation.replace(".", "/"))
        .resolve(libraryConfig.libraryName)
        .also { folder -> folder.mkdirs() }

    val plugin = pluginPath.resolve("${libraryConfig.pluginClassName}.kt").create { file ->
        AndroidPluginGenerator(
            path = file,
            methodChannelName = libraryConfig.libraryName,
            libraryConfig = libraryConfig,
            methods = methods,
        )
    }

    return AndroidFolder(
        buildGradle = buildGradle,
        settingsGradle = settingsGradle,
        srcMain = AndroidSource(
            manifest = manifest,
            plugin = plugin,
        ),
    )
}

/**
 * Copy Flutter documents from a source folder to the Flutter plugin package.
 *
 * For a Flutter plugin package there are 3 required files:
 * - README.md
 * - CHANGELOG.md
 * - LICENSE
 */
internal fun copyFlutterDoc(

    /**
     * The folder where to copy to.
     */
    outputPath: File,

    /**
     * The source file to copy.
     */
    sourceFile: File,

    /**
     * The copied filename.
     */
    filename: String,
) =
    outputPath.resolve(filename).also { file ->

        /**
         * Throw a [KlutterCodeGenerationException] if the source does not exist.
         */
        if (!sourceFile.exists()) {
            throw KlutterCodeGenerationException("${sourceFile.name} does not exist in: ${sourceFile.absolutePath}")
        }

        if (file.exists()) {
            file.delete()
        }

        file.createNewFile()
        file.writeText(sourceFile.readText())
    }

/**
 * Create new folder if it does not exist.
 */
private fun File.create(clear: Boolean = false): File {
    if (exists() && clear) deleteRecursively()
    if (!exists()) mkdirs(); return this
}

/**
 * Create new file and write content.
 */
private fun File.create(clear: Boolean = false, generator: (File) -> KlutterFileGenerator): File {
    if (exists() && clear) delete()
    if (!exists()) createNewFile()
    generator(this).generate()
    return this
}

