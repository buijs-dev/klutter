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
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.shared.Method
import dev.buijs.klutter.kore.shared.toSnakeCase

class AndroidAdapter(
    private val pluginPackageName: String,
    private val pluginClassName: String,
    methodChannels: Set<String>,
    eventChannels: Set<String>,
    controllers: Set<Controller>,
): KlutterPrinter {

    private val importsFramework = setOf(
        "import android.app.Activity",
        "import android.content.Context",
        "import io.flutter.embedding.engine.plugins.activity.ActivityAware",
        "import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding",
        "import io.flutter.embedding.engine.plugins.FlutterPlugin",
        "import io.flutter.plugin.common.EventChannel",
        "import io.flutter.plugin.common.EventChannel.EventSink",
        "import io.flutter.plugin.common.EventChannel.StreamHandler",
        "import io.flutter.plugin.common.MethodCall",
        "import io.flutter.plugin.common.MethodChannel",
        "import io.flutter.plugin.common.MethodChannel.MethodCallHandler",
        "import io.flutter.plugin.common.MethodChannel.Result",
        "import kotlinx.coroutines.CoroutineScope",
        "import kotlinx.coroutines.Dispatchers",
        "import kotlinx.coroutines.launch",
    )

    private val importsControllers = controllers
        .mapNotNull { controller -> controller.packageName }
        .map { "import $it.*" }
        .toSet()

    private val singletonControllerVariables = controllers
        .filter { it is Singleton }
        .map { it.className }
        .map{ """private val ${it.replaceFirstChar { char -> char.lowercase() }}: $it = $it()""" }
        .toSet()

    /**
     * Every BroadcastController returns a Flow which should be handled inside a Coroutine.
     */
    private val broadcastControllerScopes = controllers
        .filterIsInstance<BroadcastController>()
        .map { "    private val scope${it.className} = CoroutineScope(Dispatchers.Main)" }
        .toSet()

    /**
     * Every BroadcastController can have 0 or more subscribers.
     */
    private val broadcastControllerSubscribers = controllers
        .filterIsInstance<BroadcastController>()
        .map { "    private val subscribers${it.className} = mutableListOf<EventSink>()" }
        .toSet()

    private val broadcastReceiverFunctions = controllers
        .filterIsInstance<BroadcastController>()
        .map { controller ->
            listOf(
                "        scope${controller.className}.launch {",
                "            ${controller.eventHandlerString()}",
                "                subscribers${controller.className}.forEach { it.success(${controller.eventReturnValue()}) }",
                "            }",
                "        }",
                ""
            )
        }
        .flatten()

    private val broadcastSubscriberWhenClauses = controllers
        .filterIsInstance<BroadcastController>()
        .map { controller ->
            listOf(
                """            "${controller.className.toSnakeCase()}" ->""",
                """                subscribers${controller.className}.add(eventSink)"""
            )
        }
        .flatten()

    private val broadcastControllerCancellations = controllers
        .filterIsInstance<BroadcastController>()
        .map { controller ->
            listOf(
                """        ${controller.className.replaceFirstChar { char -> char.lowercase() }}.cancel()""",
                """        subscribers${controller.className}.clear()"""
            )
        }
        .flatten()

    private val methodChannelHandlerWhenClauses = controllers
        .filter { it.functions.isNotEmpty() }
        .flatMap { controller ->
            controller.functions.map { it.methodHandlerString(controller.instanceOrConstructor()) }
        }.sorted()

    private val methodChannelNames =
        methodChannels.map { """        "$it",""" }

    private val eventChannelNames =
        eventChannels.map { """        "$it",""" }

    override fun print(): String = buildString {
        appendLine("package $pluginPackageName")
        appendLine("")
        appendLines(importsFramework)
        appendLines(importsControllers)
        appendLine()
        appendLine("private val methodChannelNames = listOf(")
        appendLines(methodChannelNames)
        appendLine(")")
        appendLine()
        appendLine("private val eventChannelNames = listOf(")
        appendLines(eventChannelNames)
        appendLine(")")
        appendLine()
        appendLines(singletonControllerVariables)
        appendLine()
        appendLine("class $pluginClassName: FlutterPlugin, MethodCallHandler, StreamHandler, ActivityAware {")
        appendLine()
        appendLine("    private lateinit var activity: Activity")
        appendLine("    private lateinit var methodChannels: List<MethodChannel>")
        appendLine("    private lateinit var eventChannels: List<EventChannel>")
        appendLines(broadcastControllerScopes)
        appendLine("    private val mainScope = CoroutineScope(Dispatchers.Main)")
        appendLines(broadcastControllerSubscribers)
        appendLine()
        appendTemplate(
            """
                    |    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
                    |        this.methodChannels = methodChannelNames.map { name ->
                    |            val channel = MethodChannel(binding.binaryMessenger, name)
                    |            channel.setMethodCallHandler(this)
                    |            channel
                    |        }
                    |
                    |        this.eventChannels = eventChannelNames.map { name ->
                    |            val channel = EventChannel(binding.binaryMessenger, name)
                    |            channel.setStreamHandler(this)
                    |            channel
                    |        }
                    |""")
        appendLine()
        appendLines(broadcastReceiverFunctions)
        appendLine("   }")
        appendLine()
        appendTemplate(
            """
                    |    override fun onMethodCall(call: MethodCall, result: Result) {
                    |        mainScope.launch {
                    |            onEvent(
                    |                event = call.method,
                    |                data = call.arguments,
                    |                result = result
                    |            )
                    |        }
                    |    }
                    |
                    |""")
        appendLine()
        appendTemplate("""
            |    override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink) {
            |        when (arguments) {
        """)
        appendLine()
        appendLines(broadcastSubscriberWhenClauses)
        appendTemplate("""
            |            else -> {
            |                eventSink.error("Unknown topic", "${"$"}arguments", null)
            |            }
            |        }
            |    }
            |
        """)
        appendLine()
        appendLine("    override fun onCancel(arguments: Any?) {")
        appendLines(broadcastControllerCancellations)
        appendTemplate("""
            |        for (stream in eventChannels) {
            |            stream.setStreamHandler(null)
            |        }
            |    }
            |
            |    override fun onDetachedFromEngine(
            |        binding: FlutterPlugin.FlutterPluginBinding
            |    ) {
            |        for (channel in methodChannels) {
            |            channel.setMethodCallHandler(null)
            |        }
            |    }
            |
            |    override fun onAttachedToActivity(
            |        binding: ActivityPluginBinding
            |    ) {
            |        activity = binding.activity
            |    }
            |
            |    override fun onReattachedToActivityForConfigChanges(
            |        binding: ActivityPluginBinding
            |    ) {
            |        activity = binding.activity
            |    }
            |
            |    override fun onDetachedFromActivity() {
            |        // nothing
            |    }
            |
            |    override fun onDetachedFromActivityForConfigChanges() {
            |        // nothing
            |    }
            |
            |    fun <T> onEvent(event: String, data: T?, result: Result) {
            |        try {
            |            when(event) {
            """)
        appendLine()
        appendLines(methodChannelHandlerWhenClauses)
        appendTemplate("""
            |                else -> result.notImplemented()
            |            }
            |        } catch(e: Exception) {
            |            result.error("10101", e.message, e.stackTrace)
            |        }
            |    }
            |}
            |""")
    }

}

private fun BroadcastController.eventHandlerString(): String =
    "${(this as Controller).instanceOrConstructor()}.receiveBroadcastAndroid().collect { value ->"

private fun BroadcastController.eventReturnValue(): String = when(this.response) {
    is StandardType -> "value"
    is Nullable -> "value?.toKJson()"
    else -> "value.toKJson()"
}

private fun Method.methodHandlerString(instanceOrConstuctor: String): String {

    val requestArgumentOrEmpty = when(this.requestDataType) {
        null -> ""
        is StandardType -> "data"
        is Nullable -> "data?.toKJson()"
        else -> "data.toKJson()"
    }

    val responseDecoderOrEmpty = when(this.responseDataType) {
        is StandardType -> ""
        is Nullable -> "?.toKJson()"
        else -> ".toKJson()"
    }

    return """
        |                "$command" ->
        |                    result.success($instanceOrConstuctor.$method($requestArgumentOrEmpty)$responseDecoderOrEmpty)
    """.trimMargin()

}

private fun Controller.instanceOrConstructor() = when(this) {
    is Singleton -> className.replaceFirstChar { char -> char.lowercase() }
    else -> "${className}()"
}