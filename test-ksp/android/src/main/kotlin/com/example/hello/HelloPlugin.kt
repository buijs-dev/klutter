package com.example.hello

import android.app.Activity
import android.content.Context
import dev.buijs.klutter.EventChannelFacade
import dev.buijs.klutter.MethodChannelFacade
import dev.buijs.klutter.registerEventSink
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.EventChannel.StreamHandler
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.buijs.klutter.*

private val methodChannelNames = setOf(
        "com.example.hello/channel/my_simple_controller",
)

private val eventChannelNames = setOf(
        "com.example.hello/channel/my_broadcast_controller",
        "com.example.hello/channel/counter",
)

private val myBroadcastController: MyBroadcastController = MyBroadcastController()
private val counter: Counter = Counter()

class HelloPlugin: FlutterPlugin, MethodCallHandler, StreamHandler, ActivityAware {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var activity: Activity
    private lateinit var mcFacade: MethodChannelFacade
    private lateinit var ecFacade: EventChannelFacade

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        this.mcFacade = MethodChannelFacade(this, binding.binaryMessenger, methodChannelNames)
        this.ecFacade = EventChannelFacade(this, binding.binaryMessenger, eventChannelNames)
    }        

    override fun onMethodCall(call: MethodCall, result: Result) {
        mainScope.launch {
            onEvent(
                event = call.method,
                data = call.arguments,
                result = result
            )
        }
    }


    override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink) {
        when (arguments) {
            "my_broadcast_controller" ->
                 registerEventSink(myBroadcastController, eventSink)
            "counter" ->
                 registerEventSink(counter, eventSink)
            else -> {
                eventSink.error("Unknown topic", "$arguments", null)
            }
        }
    }

    override fun onCancel(arguments: Any?) {
        myBroadcastController.cancel()
        counter.cancel()
        ecFacade.cancel()
    }

    override fun onDetachedFromEngine(
        binding: FlutterPlugin.FlutterPluginBinding
    ) {
        mcFacade.cancel()
    }

    override fun onAttachedToActivity(
        binding: ActivityPluginBinding
    ) {
        activity = binding.activity
    }

    override fun onReattachedToActivityForConfigChanges(
        binding: ActivityPluginBinding
    ) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        // nothing
    }

    override fun onDetachedFromActivityForConfigChanges() {
        // nothing
    }

    suspend fun <T> onEvent(event: String, data: T?, result: Result) { 
           try {
               when(event) {
                "foo" ->
                    result.success(MySimpleController().foo())
                "getChakka" ->
                    result.success(MySimpleController().chakka().toKJson())
                "setChakka" ->
                    result.success(MySimpleController().setChakka(data as String).toKJson())
                   else -> result.notImplemented()
               }
           } catch(e: Exception) {
               result.error("10101", e.message, e.stackTrace)
           }
       }
}
