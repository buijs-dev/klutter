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
import dev.buijs.klutter.core.shared.AndroidPluginGenerator
import dev.buijs.klutter.core.shared.FlutterLibraryGenerator
import dev.buijs.klutter.core.shared.FlutterPubspecScanner
import dev.buijs.klutter.core.shared.IosPluginGenerator
import dev.buijs.klutter.core.shared.IosPodspecVisitor

class GenerateAdapterTask(
    private val android: Android,
    private val ios: IOS,
    private val root: Root,
    private val platform: Platform,
)
    : KlutterTask
{

    private var dartObjects: DartObjects? = null
    private var methods: List<MethodData>? = null

    override fun run() {
        platform.source().let {
            dartObjects = KlutterResponseProcessor(it).process()
            methods =  KlutterAdapteeScanner(it).scan(language = ReturnTypeLanguage.DART)
        }

        processPlugin()

    }

    private fun processPlugin() {

        val pubspec = FlutterPubspecScanner(root.folder.resolve("pubspec.yaml")).scan()

        val pluginName = pubspec.libraryName
        val packageName = pubspec.packageName
        val pluginPath = packageName?.replace(".", "/") ?: ""
        val pluginClassName = pubspec.pluginClassName
        val methodChannelName = packageName ?: "KLUTTER"
        methods?.let { methods ->
            FlutterLibraryGenerator(
                path = root.resolve("lib/$pluginName.dart"),
                methodChannelName = methodChannelName,
                pluginClassName = pluginClassName,
                methods = methods,
                messages = dartObjects ?: DartObjects(
                    messages = emptyList(),
                    enumerations = emptyList(),
                ),
            ).generate()

            AndroidPluginGenerator(
                path = android.file.resolve("src/main/kotlin/$pluginPath/$pluginClassName.kt"),
                methodChannelName = methodChannelName,
                pluginClassName = pluginClassName,
                libraryPackage = packageName,
                methods = methods,
            ).generate()

            IosPluginGenerator(
                path = ios.file.resolve("Classes/Swift$pluginClassName.swift"),
                methodChannelName = methodChannelName,
                pluginClassName = pluginClassName,
                methods = methods,
                frameworkName = "Platform",
            ).generate()

            IosPodspecVisitor(
                podspec = ios.file.resolve("$pluginName.podspec")
            ).visit()
        }

    }

    companion object {
        fun create(pathToRoot: String, pluginName: String? = null): GenerateAdapterTask {
            val project = KlutterProject.create(pathToRoot, pluginName)
            return GenerateAdapterTask(
                root = project.root,
                android = project.android,
                ios = project.ios,
                platform = project.platform,
            )
        }
    }
}
