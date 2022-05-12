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


import com.intellij.openapi.project.Project
import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.annotations.processor.AndroidActivityScanner
import dev.buijs.klutter.core.annotations.processor.KlutterAdapteeScanner
import dev.buijs.klutter.core.annotations.processor.KlutterResponseProcessor
import dev.buijs.klutter.core.tasks.adapter.dart.DartGenerator
import dev.buijs.klutter.core.tasks.adapter.flutter.AndroidActivityVisitor
import dev.buijs.klutter.core.tasks.adapter.flutter.AndroidAdapterGenerator
import dev.buijs.klutter.core.tasks.adapter.flutter.FlutterAdapterGenerator
import dev.buijs.klutter.core.tasks.adapter.flutter.IosAppDelegateGenerator

/**
 * @author Gillian Buijs
 */
@Suppress("unused")
class GenerateAdapterTask(
    context: Project,
    private val android: Android,
    private val ios: IOS,
    private val flutter: Flutter,
    private val platform: Platform,
    private val iosVersion: String,
)
    : KlutterTask
{

    private val androidActivity = AndroidActivityScanner(context, android).scan()
    private val dartObjects = KlutterResponseProcessor(platform.source(), context).process()
    private val methods = KlutterAdapteeScanner(platform.source(), context).scan().map {
        //MCD stores the Kotlin type so need to convert it dart type for generating the proper adapter in Dart.
        MethodCallDefinition(
            getter = it.getter,
            import = it.import,
            call = it.call,
            async = it.async,
            returns = DartKotlinMap.toMapOrNull(it.returns)?.dartType ?: it.returns,
        )
    }

    override fun run() {
        createAndroidAdapter()
        createIosAdapter()
        createFlutterAdapter()
    }

    private fun createAndroidAdapter() {
        AndroidAdapterGenerator(methods, android.app()).generate()
        AndroidActivityVisitor(androidActivity).visit()
    }

    private fun createIosAdapter() {
        IosAppDelegateGenerator(
            methods,
            ios,
            platform.podspec().nameWithoutExtension
        ).generate()
    }

    private fun createFlutterAdapter() {
        DartGenerator(flutter, dartObjects).generate()
        FlutterAdapterGenerator(flutter, methods).generate()
    }

}