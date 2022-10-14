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
package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.kore.shared.excludeArm64
import dev.buijs.klutter.kore.shared.maybeCreate
import dev.buijs.klutter.kore.shared.toChannelName
import dev.buijs.klutter.kore.shared.write
import dev.buijs.klutter.kore.templates.*

/**
 * Task to generate the boilerplate code required to let Kotlin Multiplatform and Flutter communicate.
 */
internal class GenerateAdaptersForPluginTask(
    private val android: Android,
    private val ios: IOS,
    private val root: Root,
    private val platform: Platform,
) : KlutterTask{

    private val methodChannelName = root.toPubspec().toChannelName()

    override fun run() {
        platform.collect().let {
            it.flutter(root)
            it.android(android)
            it.ios(ios)
        }
    }

    private fun AdapterData.flutter(root: Root){
        root.pathToLibFile.maybeCreate().write(
            FlutterAdapter(
                pluginClassName = root.pluginClassName,
                methodChannelName = methodChannelName,
                methods = methods,
                messages = messages,
                enumerations = enumerations,
            )
        )
        controllers.forEach { controller ->
            root.pathToLibFolder
                .resolve("${controller.name.lowercase()}_stream.dart")
                .maybeCreate()
                .write(
                    FlutterReceiverWidget(
                        channelName = methodChannelName,
                        controllerName = controller.name,
                        dataType = controller.dataType,
                    )
                )
        }
    }

    private fun AdapterData.ios(ios: IOS){
        ios.podspec().excludeArm64("dependency'Flutter'")
        ios.pathToPlugin.maybeCreate().write(
            IosAdapter(
                pluginClassName = ios.pluginClassName,
                methodChannelName = methodChannelName,
                methods = methods,
                controllers = controllers,
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
                controllers = controllers,
            )
        )
    }

}