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

import dev.buijs.klutter.core.KlutterTask
import dev.buijs.klutter.core.annotations.KlutterResponseProcessor
import dev.buijs.klutter.core.annotations.collectAnnotatedWith
import dev.buijs.klutter.core.project.*
import dev.buijs.klutter.core.shared.*
import dev.buijs.klutter.core.templates.AndroidAdapter
import dev.buijs.klutter.core.templates.FlutterAdapter
import dev.buijs.klutter.core.templates.IosAdapter
import java.io.File

/**
 * Task to generate the boilerplate code required to let Kotlin Multiplatform and Flutter communicate.
 */
class AdapterGeneratorTask(
    private val android: Android,
    private val ios: IOS,
    private val root: Root,
    private val platform: Platform,
) : KlutterTask {

    private val methodChannelName = root.toPubspec().toChannelName()

    override fun run() {
        platform.collect().let {
            it.flutter(root)
            it.android(android)
            it.ios(ios)
        }
    }

    private fun AdapterData.flutter(root: Root){
        root.pathToLib.maybeCreate().write(
            FlutterAdapter(
                pluginClassName = root.pluginClassName,
                methodChannelName = methodChannelName,
                methods = methods,
                messages = messages,
                enumerations = enumerations,
            )
        )
    }

    private fun AdapterData.ios(ios: IOS){
        ios.podspec().excludeArm64()
        ios.pathToPlugin.maybeCreate().write(
            IosAdapter(
                pluginClassName = ios.pluginClassName,
                methodChannelName = methodChannelName,
                methods = methods,
            )
        )
    }

    private fun AdapterData.android(android: Android){
        android.pathToPlugin.maybeCreate().write(
            AndroidAdapter(
                pluginClassName = android.pluginClassName,
                pluginPackageName = android.pluginPackageName,
                methodChannelName = methodChannelName,
                methods = methods,
            )
        )
    }
}

/**
 * Utility to scan a Klutter project and collect it's metadata.
 *
 * The metadata is required to generate method channel code in:
 * - root/lib
 * - root/android
 * - root/ios
 */
internal fun Platform.collect(): AdapterData {

    val source = source()

    // Scan platform module for @KlutterAdaptee.
    val methods = source.scanForKlutterAdaptee()

    // Scan for any class annotated with @KlutterResponse.
    val processor = KlutterResponseProcessor(source)

    // Response classes annotated with @KlutterResponse.
    val messages = processor.messages

    // Enumerations annotated with @KlutterResponse.
    val enumerations = processor.enumerations

    return AdapterData(
        methods = methods,
        messages = messages,
        enumerations = enumerations,
    )
}

/**
 * Metadata containing all required information to generated method channel code.
 */
internal data class AdapterData(

    /**
     * List of @KlutterAdaptee annotated methods.
     */
    val methods: List<Method>,

    /**
     * List of custom data transfer objects annotated with @KlutterResponse defined in the platform module.
     */
    val messages: List<DartMessage>,

    /**
     * List of enumerations annotated with @KlutterResponse defined in the platform module.
     */
    val enumerations: List<DartEnum>,
)

internal fun File.scanForKlutterAdaptee(
    language: Language = Language.DART,
): List<Method> = this
    .collectAnnotatedWith("@KlutterAdaptee")
    .map { it.toMethods(language) }
    .flatten()