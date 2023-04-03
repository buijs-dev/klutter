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

package dev.buijs.klutter.kore.template

import dev.buijs.klutter.kore.ast.AbstractType
import dev.buijs.klutter.kore.ast.BooleanType
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.ast.CustomType
import dev.buijs.klutter.kore.ast.IntType
import dev.buijs.klutter.kore.ast.NullableCustomType
import dev.buijs.klutter.kore.ast.NullableStringType
import dev.buijs.klutter.kore.ast.RequestScopedBroadcastController
import dev.buijs.klutter.kore.ast.RequestScopedController
import dev.buijs.klutter.kore.ast.SingletonBroadcastController
import dev.buijs.klutter.kore.ast.SingletonController
import dev.buijs.klutter.kore.ast.StringType
import dev.buijs.klutter.kore.ast.TypeMember
import dev.buijs.klutter.kore.shared.Method
import dev.buijs.klutter.kore.templates.AndroidAdapter
import dev.buijs.klutter.kore.test.TestUtil
import spock.lang.Specification

class AndroidAdapterSpec extends Specification {

    def "AndroidAdapter should create a valid Kotlin class"() {
        given:
        def packageName = "dev.buijs.platform.controller"
        def sut = createSut([
                new RequestScopedController(packageName, "MySimpleContollerImpl", [
                        new Method(
                                "sayHi",
                                "dev.buijs.platform.contoller.MySimpleControllerImpl",
                                "sayHiPlease",
                                false,
                                new StringType(),
                                new StringType()
                        )
                ]),
                new SingletonBroadcastController(packageName, "MyEmittingController", [
                        new Method(
                                "start",
                                "dev.buijs.platform.contoller.MyEmittingController",
                                "startBroadcast",
                                false,
                                new BooleanType(),
                                null
                        )
                ],
                        new CustomType("MyResponse", "dev.buijs.platform.controller",
                                [new TypeMember("foo", new StringType())])
        )].toSet())

        expect:
        println sut.print()
        TestUtil.verify(sut.print(), classBody)

        where:
        classBody = """
package super_plugin

import android.app.Activity
import android.content.Context
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
import dev.buijs.platform.controller.*

private val methodChannelNames = listOf(
        "dev.company.plugins/channels/controller",
)

private val eventChannelNames = listOf(
        "dev.buijs.plugins/streams/controller",
)

private val myEmittingController: MyEmittingController = MyEmittingController()

class SuperPlugin: FlutterPlugin, MethodCallHandler, StreamHandler, ActivityAware {

    private lateinit var activity: Activity
    private lateinit var methodChannels: List<MethodChannel>
    private lateinit var eventChannels: List<EventChannel>
    private val scopeMyEmittingController = CoroutineScope(Dispatchers.Main)
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val subscribersMyEmittingController = mutableListOf<EventSink>()

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        this.methodChannels = methodChannelNames.map { name ->
            val channel = MethodChannel(binding.binaryMessenger, name)
            channel.setMethodCallHandler(this)
            channel
        }

        this.eventChannels = eventChannelNames.map { name ->
            val channel = EventChannel(binding.binaryMessenger, name)
            channel.setStreamHandler(this)
            channel
        }

        scopeMyEmittingController.launch {
            myEmittingController.receiveBroadcastAndroid().collect { value ->
                subscribersMyEmittingController.forEach { it.success(value.toKJson()) }
            }
        }

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
            "my_emitting_controller" ->
                subscribersMyEmittingController.add(eventSink)
            else -> {
                eventSink.error("Unknown topic", "\$arguments", null)
            }
        }
    }

    override fun onCancel(arguments: Any?) {
        myEmittingController.cancel()
        subscribersMyEmittingController.clear()
        for (stream in eventChannels) {
            stream.setStreamHandler(null)
        }
    }

    override fun onDetachedFromEngine(
        binding: FlutterPlugin.FlutterPluginBinding
    ) {
        for (channel in methodChannels) {
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

    fun <T> onEvent(event: String, data: T?, result: Result) {
        try {
            when(event) {
                "sayHi" ->
                    result.success(MySimpleContollerImpl().sayHiPlease(data))
                "start" ->
                    result.success(myEmittingController.startBroadcast())
                else -> result.notImplemented()
            }
        } catch(e: Exception) {
            result.error("10101", e.message, e.stackTrace)
        }
    }
}
"""
    }

    def "Verify broadcast - singleton - standard-type"() {
        expect:
        with(createSut(Set.of(createSingletonBroadcastController([],stringType)))) {
            with(it.print()) { content ->
                content.contains("mySingletonBroadcastController.receiveBroadcastAndroid().collect { value ->")
                content.contains("subscribersMySingletonBroadcastController.forEach { it.success(value) }")
            }
        }
    }

    def "Verify broadcast - singleton - nullable standard-type"() {
        expect:
        with(createSut(Set.of(createSingletonBroadcastController([],nullableStringType)))) {
            with(it.print()) { content ->
                content.contains("mySingletonBroadcastController.receiveBroadcastAndroid().collect { value ->")
                content.contains("subscribersMySingletonBroadcastController.forEach { it.success(value) }")
            }
        }
    }

    def "Verify broadcast - singleton - custom-type"() {
        expect:
        with(createSut(Set.of(createSingletonBroadcastController([],myCustomType)))) {
            with(it.print()) { content ->
                content.contains("mySingletonBroadcastController.receiveBroadcastAndroid().collect { value ->")
                content.contains("subscribersMySingletonBroadcastController.forEach { it.success(value.toKJson()) }")
            }
        }
    }

    def "Verify broadcast - request-scoped - nullable custom-type"() {
        expect:
        with(createSut(Set.of(createRequestScopedBroadcastController([],nullableMyCustomType)))) {
            with(it.print()) { content ->
                content.contains("MyRequestScopedBroadcastController().receiveBroadcastAndroid().collect { value ->")
                content.contains("subscribersMyRequestScopedBroadcastController.forEach { it.success(value?.toKJson()) }")
            }
        }
    }

    def "Verify simple - singleton - standard-type not-null request, standard-type not-null response"() {
        expect:
        with(createSut(Set.of(createSingletonController([createMethod(stringType, stringType)])))) {
            with(it.print()) { content ->
                content.contains('''"sayHi" ->''')
                content.contains("result.success(mySingletonController.sayHiPlease(data))")
            }
        }
    }

    def "Verify simple - singleton - standard-type nullable request, standard-type not-null response"() {
        expect:
        with(createSut(Set.of(createSingletonController([createMethod(nullableStringType, stringType)])))) {
            with(it.print()) { content ->
                content.contains('''"sayHi" ->''')
                content.contains("result.success(mySingletonController.sayHiPlease(data))")
            }
        }
    }

    def "Verify simple - singleton - standard-type nullable request, standard-type nullable response"() {
        expect:
        with(createSut(Set.of(createSingletonController([createMethod(nullableStringType, nullableStringType)])))) {
            with(it.print()) { content ->
                content.contains('''"sayHi" ->''')
                content.contains("result.success(mySingletonController.sayHiPlease(data))")
            }
        }
    }

    def "Verify simple - request-scoped - no request, custom-type not-null response"() {
        expect:
        with(createSut(Set.of(createRequestScopedController([createMethod(null, myCustomType)])))) {
            with(it.print()) { content ->
                content.contains('''"sayHi" ->''')
                content.contains("result.success(MyRequestScopedController().sayHiPlease().toKJson())")
            }
        }
    }

    def "Verify simple - request-scoped - custom-type not-null request, custom-type not-null response"() {
        expect:
        with(createSut(Set.of(createRequestScopedController([createMethod(myCustomType, myCustomType)])))) {
            with(it.print()) { content ->
                content.contains('''"sayHi" ->''')
                content.contains("result.success(MyRequestScopedController().sayHiPlease(data.toKJson()).toKJson())")
            }
        }
    }

    def "Verify simple - request-scoped - custom-type nullable request, custom-type not-null response"() {
        expect:
        with(createSut(Set.of(createRequestScopedController([createMethod(nullableMyCustomType, myCustomType)])))) {
            with(it.print()) { content ->
                content.contains('''"sayHi" ->''')
                content.contains("result.success(MyRequestScopedController().sayHiPlease(data?.toKJson()).toKJson())")
            }
        }
    }

    def "Verify simple - request-scoped - custom-type nullable request, custom-type nullable response"() {
        expect:
        with(createSut(Set.of(createRequestScopedController([createMethod(nullableMyCustomType, nullableMyCustomType)])))) {
            with(it.print()) { content ->
                content.contains('''"sayHi" ->''')
                content.contains("result.success(MyRequestScopedController().sayHiPlease(data?.toKJson())?.toKJson())")
            }
        }
    }

    def static stringType = new StringType()

    def static nullableStringType = new NullableStringType()

    def static integerType = new IntType()

    def static myCustomType = new CustomType(
            "MyCustomType",
            "dev.buijs.platform.models",
            [new TypeMember("foo", integerType)]
    )

    def static nullableMyCustomType = new NullableCustomType(
            "MyCustomType",
            "dev.buijs.platform.models",
            [new TypeMember("foo", integerType)]
    )

    def static createMethod(
            AbstractType request,
            AbstractType response
    ) {
        new Method(
                "sayHi",
                "dev.buijs.platform.controller.MySimpleControllerImpl",
                "sayHiPlease",
                false,
                response,
                request
        )
    }

    def static createRequestScopedController(List<Method> methods) {
        new RequestScopedController(
                "dev.buijs.platform.controller",
                "MyRequestScopedController",
                methods
        )
    }

    def static createRequestScopedBroadcastController(List<Method> methods, AbstractType response) {
        new RequestScopedBroadcastController(
                "dev.buijs.platform.controller",
                "MyRequestScopedBroadcastController",
                methods,
                response
        )
    }

    def static createSingletonController(List<Method> methods) {
        new SingletonController(
                "dev.buijs.platform.controller",
                "MySingletonController",
                methods
        )
    }

    def static createSingletonBroadcastController(List<Method> methods, AbstractType response) {
        new SingletonBroadcastController(
                "dev.buijs.platform.controller",
                "MySingletonBroadcastController",
                methods,
                response
        )
    }

    def static createSut(
            Set<Controller> controllers
    ) {
        def packageName = "super_plugin"
        def pluginName = "SuperPlugin"
        def channels = ["dev.company.plugins/channels/controller"].toSet()
        def streams = ["dev.buijs.plugins/streams/controller"].toSet()
        new AndroidAdapter(
                packageName,
                pluginName,
                channels,
                streams,
                controllers
        )
    }

}