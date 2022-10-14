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

import dev.buijs.klutter.kore.TestData
import dev.buijs.klutter.kore.shared.ControllerData
import dev.buijs.klutter.kore.templates.AndroidAdapter
import dev.buijs.klutter.kore.test.TestUtil
import spock.lang.Ignore
import spock.lang.Specification

@Ignore // TODO
class AndroidAdapterSpec extends Specification {

    def "AndroidBroadcastAdapter should create a valid Kotlin class"() {
        given:
        def packageName = "super_plugin"
        def pluginName = "SuperPlugin"
        def channelName = "dev.company.plugins.super_plugins"
        def methods = [TestData.greetingMethod]
        def controllers = [new ControllerData("Greeting", "Greeting")]

        and: "The printer as SUT"
        def adapter = new AndroidAdapter(
                packageName, pluginName, channelName, methods, controllers
        )

        expect:
        TestUtil.verify(adapter.print(), classBody)

        where:
        classBody = """package super_plugin
            
                        import platform.Greeting
                        import android.app.Activity
                        import androidx.annotation.NonNull
                        import io.flutter.embedding.engine.plugins.FlutterPlugin
                        import io.flutter.embedding.engine.plugins.activity.ActivityAware
                        import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
                        import io.flutter.plugin.common.EventChannel
                        import io.flutter.plugin.common.MethodCall
                        import io.flutter.plugin.common.MethodChannel.MethodCallHandler
                        import kotlinx.coroutines.CoroutineScope
                        import kotlinx.coroutines.Dispatchers
                        import kotlinx.coroutines.launch
                        
                        /** SuperPlugin */
                        class SuperPlugin: FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware {

                          private lateinit var channel : MethodChannel
                           
                          private val mainScope = CoroutineScope(Dispatchers.Main) 
                           
                          private lateinit var activity: Activity
                           private val greeting = Greeting()
                           private lateinit var greetingEventsChannel : EventChannel
    
                          override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
                            channel = MethodChannel(flutterPluginBinding.binaryMessenger, "dev.company.plugins.super_plugins")
                            channel.setMethodCallHandler(this)
                            
                            greetingEventsChannel = EventChannel(flutterPluginBinding.binaryMessenger, "dev.company.plugins.super_plugins/stream/greeting")
                            greetingEventsChannel.setStreamHandler(this)
                          }
                        
                          override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
                                val method = call.method
                                mainScope.launch {
                                   when (method) {
                                        "_#!greeting!#_greeting" -> 
                                            result.success(greeting.greeting())
                                        else -> result.notImplemented()
                                        
                                   }
                                }
                          }
                        
                          override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
                            channel.setMethodCallHandler(null)
                          }
                          
                          override fun onDetachedFromActivity() {
                           // nothing
                          }
                        
                          override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
                            activity = binding.activity
                          }
                        
                          override fun onAttachedToActivity(binding: ActivityPluginBinding) {
                            activity = binding.activity
                          }
                        
                          override fun onDetachedFromActivityForConfigChanges() {
                            // nothing
                           }
                           
                          override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
                                mainScope.launch {
                                    greeting.receiveBroadcastAndroid().collect { value ->
                                        eventSink?.success(value.toKJson())
                                    }
                                }
                            }
                        
                            override fun onCancel(arguments: Any?) {
                                greetingEventsChannel.setStreamHandler(null)
                                greeting.cancel()
                            }
                        }
                        """
    }

}