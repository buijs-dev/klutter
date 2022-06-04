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

package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.utils.DefaultWriter
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class AndroidPluginGenerator(
    private val path: File,
    private val methodChannelName: String,
    private val pluginClassName: String? = null,
    private val libraryPackage: String? = null,
    private val methods: List<MethodData>,
): KlutterFileGenerator() {

    override fun printer() = AndroidPluginPrinter(
        pluginClassName = pluginClassName ?: "",
        libraryPackage = libraryPackage ?: "",
        methodChannelName = methodChannelName,
        methods = methods,
    )

    override fun writer() = DefaultWriter(path, printer().print())

}

internal class AndroidPluginPrinter(
    private val libraryPackage: String,
    private val pluginClassName: String,
    private val methodChannelName: String,
    private val methods: List<MethodData>,
): KlutterPrinter {

    override fun print(): String {

        val block = if (methods.isEmpty()) {
            "             return result.notImplemented()"
        } else {
            val defs = methods.joinToString("\n") { printFun(it) }
            "$defs \n                else -> result.notImplemented()"
        }

        val imports = methods
            .map { it.import }
            .distinct()
            .joinToString("\n") { "import $it" }

        return """
            |package $libraryPackage
            |
            |$imports
            |import androidx.annotation.NonNull
            |
            |import io.flutter.embedding.engine.plugins.FlutterPlugin
            |import io.flutter.plugin.common.MethodCall
            |import io.flutter.plugin.common.MethodChannel
            |import io.flutter.plugin.common.MethodChannel.MethodCallHandler
            |import io.flutter.plugin.common.MethodChannel.Result
            |import kotlinx.coroutines.CoroutineScope
            |import kotlinx.coroutines.Dispatchers
            |import kotlinx.coroutines.launch
            |
            |/** $pluginClassName */
            |class ${pluginClassName}: FlutterPlugin, MethodCallHandler {
            |  /// The MethodChannel that will the communication between Flutter and native Android
            |  ///
            |  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
            |  /// when the Flutter Engine is detached from the Activity
            |  private lateinit var channel : MethodChannel
            |   
            |  private val mainScope = CoroutineScope(Dispatchers.Main) 
            |   
            |  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
            |    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "$methodChannelName")
            |    channel.setMethodCallHandler(this)
            |  }
            |
            |  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
            |        mainScope.launch {
            |           when (call.method) {
            |$block
            |           }
            |        }
            |  }
            |
            |  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
            |    channel.setMethodCallHandler(null)
            |  }
            |}
            |""".trimMargin()
    }

    private fun printFun(definition: MethodData): String {
        val type = if (DartKotlinMap.toMapOrNull(definition.returns) == null) {
            ".toKJson()"
        } else ""

        return  "                \"${definition.getter}\" -> {\r\n" +
                "                    result.success(${definition.call}${type})\r\n" +
                "                }"
    }

}