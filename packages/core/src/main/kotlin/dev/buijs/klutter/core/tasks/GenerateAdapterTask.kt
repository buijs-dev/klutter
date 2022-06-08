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

package dev.buijs.klutter.core.tasks


import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.annotations.KlutterAdapteeScanner
import dev.buijs.klutter.core.annotations.KlutterResponseProcessor
import dev.buijs.klutter.core.annotations.ReturnTypeLanguage
import dev.buijs.klutter.core.shared.*
import dev.buijs.klutter.core.shared.AndroidPluginGenerator
import dev.buijs.klutter.core.shared.FlutterLibraryGenerator
import dev.buijs.klutter.core.shared.FlutterPubspecScanner
import dev.buijs.klutter.core.shared.IosPluginGenerator
import java.io.File

/**
 * Task to generate the boilerplate code required to let Kotlin Multiplatform and Flutter communicate.
 */
class GenerateAdapterTask(
    private val android: Android,
    private val ios: IOS,
    private val root: Root,
    private val platform: Platform,
) : KlutterTask {

    override fun run() {
        val source = platform.source()
        val processor = KlutterResponseProcessor(source)
        val messages = processor.messages
        val enumerations = processor.enumerations
        val methods =  KlutterAdapteeScanner(source).scan(language = ReturnTypeLanguage.DART)
        val data = GenerationData(
            root = root,
            messages = messages,
            enumerations = enumerations,
            methods = methods,
        )

        val pubspec = FlutterPubspecScanner(root.folder.resolve("pubspec.yaml")).scan()
        data.createFlutterLibrary(pubspec)
        data.createAndroidPlugin(pubspec, android)
        data.createIosPlugin(pubspec, ios)
    }


}

internal data class GenerationData(
    val root: Root,
    val messages: List<DartMessage>,
    val enumerations: List<DartEnum>,
    val methods: List<Method>,
)

internal fun GenerationData.createFlutterLibrary(pubspec: PubspecData) {
    FlutterLibraryGenerator(
        path = root.flutterLibrary(pubspec.libraryName),
        methodChannelName = pubspec.methodChannelName(),
        pluginClassName = pubspec.pluginClassName,
        methods = methods,
        messages = messages,
        enumerations = enumerations,
    ).generate()
}

internal fun GenerationData.createAndroidPlugin(pubspec: PubspecData, android: Android) {
    AndroidPluginGenerator(
        path = android.pluginClassName(
            pubspec.packageName.toPath(),
            pubspec.pluginClassName,
        ),
        methodChannelName = pubspec.methodChannelName(),
        pluginClassName = pubspec.pluginClassName,
        libraryPackage = pubspec.packageName,
        methods = methods,
    ).generate()
}

internal fun GenerationData.createIosPlugin(pubspec: PubspecData, ios: IOS) {
    IosPluginGenerator(
        path = ios.pluginClassName(pubspec.pluginClassName),
        methodChannelName = pubspec.methodChannelName(),
        pluginClassName = pubspec.pluginClassName,
        methods = methods,
        frameworkName = "Platform",
    ).generate()

    ios.folder.resolve("${pubspec.libraryName}.podspec").excludeArm64()
}

private fun IOS.pluginClassName(pluginClassName: String): File =
    folder.resolve("Classes/Swift$pluginClassName.swift")

private fun Android.pluginClassName(packagePath: String, pluginClassName: String): File =
    folder.resolve("src/main/kotlin/$packagePath/$pluginClassName.kt")

private fun Root.flutterLibrary(name: String): File =
    resolve("lib/$name.dart")

private fun PubspecData.methodChannelName(): String =
    packageName ?: "KLUTTER"

private fun String?.toPath(): String =
    this?.replace(".", "/") ?: ""