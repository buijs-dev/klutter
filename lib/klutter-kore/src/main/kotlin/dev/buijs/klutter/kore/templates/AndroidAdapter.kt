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
package dev.buijs.klutter.kore.templates

import dev.buijs.klutter.kore.KlutterPrinter
import dev.buijs.klutter.kore.shared.ControllerData
import dev.buijs.klutter.kore.shared.Method
import dev.buijs.klutter.kore.shared.postFix

class AndroidAdapter(
    private val pluginPackageName: String,
    private val pluginClassName: String,
    private val methodChannelName: String,
    private val methods: List<Method>,
    private val controllers: List<ControllerData>,
): KlutterPrinter {

    override fun print(): String = """
        |package $pluginPackageName
        |
        |${methods.printImports()}
        |import android.app.Activity
        |import androidx.annotation.NonNull
        |import io.flutter.embedding.engine.plugins.FlutterPlugin
        |import io.flutter.embedding.engine.plugins.activity.ActivityAware
        |import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        |import io.flutter.plugin.common.EventChannel
        |import io.flutter.plugin.common.MethodCall
        |import io.flutter.plugin.common.MethodChannel.MethodCallHandler
        |import kotlinx.coroutines.CoroutineScope
        |import kotlinx.coroutines.Dispatchers
        |import kotlinx.coroutines.launch
        |
        |/** $pluginClassName */
        |class ${pluginClassName}: FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware {
        |
        |    private lateinit var channel : MethodChannel
        |    
        |    private val mainScope = CoroutineScope(Dispatchers.Main)
        |     
        |    private lateinit var activity: Activity
        ${controllers.printControllerInstance()}
        ${controllers.printControllerEventChannel()}
        |
        |  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        |    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "$methodChannelName")
        |    channel.setMethodCallHandler(this)
        ${controllers.printControllerEventChannelSetter()}
        |  }
        |
        |  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        |        val method = call.method
        |        mainScope.launch {
        |           when (method) {
        ${methods.printFunctionBodies() ?: "             return result.notImplemented()"}
        |           }
        |        }
        |  }
        |
        |  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        |        channel.setMethodCallHandler(null)
        |  }
        |  
        |  override fun onDetachedFromActivity() {
        |   // nothing
        |  }
        |
        |  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        |    activity = binding.activity
        |  }
        |
        |  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        |    activity = binding.activity
        |  }
        |
        |  override fun onDetachedFromActivityForConfigChanges() {
        |    // nothing
        |   }
        |   
        |   override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
        ${controllers.printControllerListeners()}
        |    }
        |
        |    override fun onCancel(arguments: Any?) {
        ${controllers.printControllerCancellers()}
        |    }
        |}
        |""".trimMargin()

    private fun List<ControllerData>.printControllerEventChannelSetter() =
        this.joinToString("\n") { """|
        |        ${it.lowercaseName()}EventsChannel = EventChannel(flutterPluginBinding.binaryMessenger, "${methodChannelName}/stream/${it.lowercaseName()}")
        |        ${it.lowercaseName()}EventsChannel.setStreamHandler(this)
    """.trimMargin() }
}

private fun List<Method>.printImports(): String = map { it.import }
    .distinct()
    .joinToString("\n") { "import $it" }

private fun List<Method>.printFunctionBodies(): String? =
    if(isEmpty()) { null } else  {
        joinToString("\n") { """
                |                "${it.methodId}" ->
                |                    result.success(${it.method
                                                        .replaceFirst("(", "")
                                                        .replaceFirst(")", "")
                                                        .replaceFirstChar { char -> char.lowercase() }})
            """.trimMargin()}
            .postFix(" \n                else -> result.notImplemented()")
    }

private fun List<ControllerData>.printControllerInstance() =
    this.joinToString("\n") { "    private val ${it.lowercaseName()} = ${it.name}()" }

private fun List<ControllerData>.printControllerEventChannel() =
    this.joinToString("\n") { "    private lateinit var ${it.lowercaseName()}EventsChannel : EventChannel" }

private fun List<ControllerData>.printControllerCancellers() =
    this.joinToString("\n") { """|
        |        ${it.lowercaseName()}EventsChannel.setStreamHandler(null)
        |        ${it.lowercaseName()}.cancel()
    """.trimMargin() }

private fun List<ControllerData>.printControllerListeners() =
    this.joinToString("\n") { """|
        |        mainScope.launch {
        |            ${it.lowercaseName()}.receiveBroadcastAndroid().collect { value ->
        |                eventSink?.success(value?.toKJson())
        |            }
        |       }
    """.trimMargin() }