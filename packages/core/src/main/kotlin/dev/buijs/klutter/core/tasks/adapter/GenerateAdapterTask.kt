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

package dev.buijs.klutter.core.tasks.adapter


import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.annotations.processor.AndroidActivityScanner
import dev.buijs.klutter.core.annotations.processor.KlutterAdapteeScanner
import dev.buijs.klutter.core.annotations.processor.KlutterResponseProcessor
import dev.buijs.klutter.core.annotations.processor.ReturnTypeLanguage
import dev.buijs.klutter.core.tasks.adapter.dart.DartGenerator
import dev.buijs.klutter.core.tasks.adapter.flutter.AndroidActivityVisitor
import dev.buijs.klutter.core.tasks.adapter.flutter.AndroidAdapterGenerator
import dev.buijs.klutter.core.tasks.adapter.flutter.FlutterAdapterGenerator
import dev.buijs.klutter.core.tasks.adapter.flutter.IosAppDelegateGenerator
import dev.buijs.klutter.core.tasks.plugin.android.AndroidPluginGenerator
import dev.buijs.klutter.core.tasks.plugin.flutter.FlutterLibraryGenerator
import dev.buijs.klutter.core.tasks.plugin.flutter.FlutterPubspecScanner
import dev.buijs.klutter.core.tasks.plugin.ios.IosPluginSwiftGenerator

/**
 * @author Gillian Buijs
 */
class GenerateAdapterTask(
    private val android: Android,
    private val ios: IOS,
    private val flutter: Flutter,
    private val platform: Platform,
    private val libName: String = "main",
    private val isPlugin: Boolean = false,
)
    : KlutterTask
{

    private val androidActivity = AndroidActivityScanner(android).scan()
    private var dartObjects: DartObjects? = null
    private var methods: List<MethodData>? = null

    override fun run() {
        platform.source()?.let {
            dartObjects = KlutterResponseProcessor(it).process()
            methods =  KlutterAdapteeScanner(it).scan(language = ReturnTypeLanguage.DART)
        }

        if(isPlugin) {
            processPlugin()
        } else {
            processProject()
        }
    }

    private fun processPlugin() {

        val pubspec = FlutterPubspecScanner(flutter
            .root
            .folder
            .resolve("pubspec.yaml")).scan()

        val pluginName = pubspec.libraryName
        val packageName = pubspec.packageName
        val pluginPath = packageName?.replace(".", "/") ?: ""
        val pluginClassName = pubspec.pluginClassName
        val methodChannelName = packageName ?: "KLUTTER"
        methods?.let { methods ->
            FlutterLibraryGenerator(
                path = flutter.file.resolve("$pluginName.dart"),
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

            IosPluginSwiftGenerator(
                path = ios.file.resolve("Classes/Swift$pluginClassName.swift"),
                methodChannelName = methodChannelName,
                pluginClassName = pluginClassName,
                methods = methods,
            ).generate()
        }

    }

    private fun processProject() {
        android.app()?.let {
            AndroidAdapterGenerator(methods ?: emptyList(), it).generate()
            androidActivity?.let {
                    activity -> AndroidActivityVisitor(activity).visit()
            }
        }

        platform.podspec()?.let {
            IosAppDelegateGenerator(
                methods ?: emptyList(),
                ios,
                it.nameWithoutExtension
            ).generate()
        }

        dartObjects?.let { DartGenerator(flutter, it).generate() }
        methods?.let { FlutterAdapterGenerator(flutter, it, libName).generate() }
    }

}