package com.example.hello

import androidx.annotation.NonNull
import android.app.Activity
import android.content.Context
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import dev.buijs.klutter.*

private val myBroadcastController: MyBroadcastController = MyBroadcastController()
private val counter: Counter = Counter()

class HelloPlugin:
    FlutterPlugin,
    MethodCallHandler,
    EventChannel.StreamHandler,
    ActivityAware
{
      
    private val mainScope = CoroutineScope(Dispatchers.Main)

    private val bindings = loadBindings()

    private lateinit var channels: List<MethodChannel>

    private lateinit var streams: List<EventChannel>

    private lateinit var activity: Activity

    override fun onAttachedToEngine(
        @NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding
    ) {
        this.channels = bindings.channels.map { name ->
            MethodChannel(flutterPluginBinding.binaryMessenger, name).also { channel ->
                channel.setMethodCallHandler(this)
            }
        }

        this.streams = bindings.streams.map { name ->
            EventChannel(flutterPluginBinding.binaryMessenger, name).also { channel ->
                channel.setMethodCallHandler(this)
            }
        }
    }

    override fun onMethodCall(
        @NonNull call: MethodCall, 
        @NonNull result: Result
    ) {
        mainScope.launch {
            HelloMediator.onEvent(
                event = call.method,
                data = call.data,
                result = result
            )
        }
    }

    override fun onListen(
        arguments: Any?,
        eventSink: EventChannel.EventSink?
    ) {
        mainScope.launch {
            HelloMediator.onEvent(eventSink = eventSink)
        }
    }

    override fun onCancel(arguments: Any?) {
        for (stream in streams) {
            stream.setStreamHandler(null)
        }
    }
    
    override fun onDetachedFromEngine(
        @NonNull binding: FlutterPlugin.FlutterPluginBinding
    ) {
        for (channel in channels) {
            channel.setMethodCallHandler(null)
        }
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

    private fun fun loadBindings() = Bindings(
        channels = listOf(
            "com.example.hello/channel/my_simple_controller",
        ),
        streams = listOf(
            "com.example.hello/channel/my_broadcast_controller",
            "com.example.hello/channel/counter",
        )
    )
    
    fun <T> onEvent(event: String, data: T?, result: Result) {
        try {
            when(event) {
        "foo" ->
    result.succes(MySimpleController().foo())

"getChakka" ->
    result.succes(MySimpleController().chakka().toKJson())

"setChakka" ->
    result.succes(MySimpleController().setChakka(data).toKJson())
                else -> result.notImplemented()
            }
        } catch(e: Exception) {
            result.error("10101", e.message, e.stackTrace)
        }
    }

    fun onEvent(eventSink: EventChannel.EventSink?) {
        myBroadcastController.receiveBroadcastAndroid().collect { value ->
   eventSink?.success(value.toKJson())
}
counter.receiveBroadcastAndroid().collect { value ->
   eventSink?.success(value)
}
    }
}
