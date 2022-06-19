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

package dev.buijs.klutter.core.template

import dev.buijs.klutter.core.CoreTestUtil
import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.TestData
import dev.buijs.klutter.core.project.AndroidKt
import dev.buijs.klutter.core.shared.DartEnum
import dev.buijs.klutter.core.shared.DartField
import dev.buijs.klutter.core.shared.DartMessage
import dev.buijs.klutter.core.tasks.AdapterGeneratorTaskKt
import dev.buijs.klutter.core.templates.AndroidAdapter
import spock.lang.Specification

class AndroidAdapterSpec extends Specification {

    def "[toPath] verify conversion"() {
        expect:
        AndroidKt.toPath(input) == output

        where:
        input       | output
        null        | ""
        ""          | ""
        "   "       | "   "
        "blablabl"  | "blablabl"
        "bla.blab"  | "bla/blab"
    }

    def "AndroidAdapter should create a valid Kotlin class"() {
        given:
        def packageName = "super_plugin"
        def pluginName = "SuperPlugin"
        def channelName = "dev.company.plugins.super_plugins"
        def methods = [TestData.greetingMethod]

        and: "The printer as SUT"
        def adapter = new AndroidAdapter(packageName, pluginName, channelName, methods)

        expect:
        CoreTestUtil.verify(adapter, classBody)

        where:
        classBody = """package super_plugin
            
                        import platform.Greeting
                        import androidx.annotation.NonNull
                        
                        import io.flutter.embedding.engine.plugins.FlutterPlugin
                        import io.flutter.plugin.common.MethodCall
                        import io.flutter.plugin.common.MethodChannel
                        import io.flutter.plugin.common.MethodChannel.MethodCallHandler
                        import io.flutter.plugin.common.MethodChannel.Result
                        import kotlinx.coroutines.CoroutineScope
                        import kotlinx.coroutines.Dispatchers
                        import kotlinx.coroutines.launch
                        
                        /** SuperPlugin */
                        class SuperPlugin: FlutterPlugin, MethodCallHandler {
                          /// The MethodChannel that will the communication between Flutter and native Android
                          ///
                          /// This local reference serves to register the plugin with the Flutter Engine and unregister it
                          /// when the Flutter Engine is detached from the Activity
                          private lateinit var channel : MethodChannel
                           
                          private val mainScope = CoroutineScope(Dispatchers.Main) 
                           
                          override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
                            channel = MethodChannel(flutterPluginBinding.binaryMessenger, "dev.company.plugins.super_plugins")
                            channel.setMethodCallHandler(this)
                          }
                        
                          override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
                                mainScope.launch {
                                   when (call.method) {
                                        "greeting" -> {
                                            result.success(Greeting().greeting())
                                        } 
                                        else -> result.notImplemented()
                                   }
                                }
                          }
                        
                          override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
                            channel.setMethodCallHandler(null)
                          }
                        }
                        """
    }

    def "AndroidAdapter should create a valid Kotlin class when the methods list is empty"() {
        given:
        def packageName = "super_plugin"
        def pluginName = "SuperPlugin"
        def channelName = "dev.company.plugins.super_plugins"
        def methods = []

        and: "The printer as SUT"
        def adapter = new AndroidAdapter(packageName, pluginName, channelName, methods)

        expect:
        CoreTestUtil.verify(adapter, classBody)

        where:
        classBody = """package super_plugin
            
                        import androidx.annotation.NonNull
                        
                        import io.flutter.embedding.engine.plugins.FlutterPlugin
                        import io.flutter.plugin.common.MethodCall
                        import io.flutter.plugin.common.MethodChannel
                        import io.flutter.plugin.common.MethodChannel.MethodCallHandler
                        import io.flutter.plugin.common.MethodChannel.Result
                        import kotlinx.coroutines.CoroutineScope
                        import kotlinx.coroutines.Dispatchers
                        import kotlinx.coroutines.launch
                        
                        /** SuperPlugin */
                        class SuperPlugin: FlutterPlugin, MethodCallHandler {
                          /// The MethodChannel that will the communication between Flutter and native Android
                          ///
                          /// This local reference serves to register the plugin with the Flutter Engine and unregister it
                          /// when the Flutter Engine is detached from the Activity
                          private lateinit var channel : MethodChannel
                           
                          private val mainScope = CoroutineScope(Dispatchers.Main) 
                           
                          override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
                            channel = MethodChannel(flutterPluginBinding.binaryMessenger, "dev.company.plugins.super_plugins")
                            channel.setMethodCallHandler(this)
                          }
                        
                          override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
                                mainScope.launch {
                                   when (call.method) {
                                       return result.notImplemented()
                                   }
                                }
                          }
                        
                          override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
                            channel.setMethodCallHandler(null)
                          }
                        }
                        """
    }

    def "Validate throws exception if customDataType list is not empty after validating"() {
        given:
        def message1 = new DartMessage("Bob",
                [new DartField("FartCannon", "", false, false, true)]
        )

        def message2 = new DartMessage("Dave",
                [new DartField("String", "", false, false, false)]
        )

        when:
        AdapterGeneratorTaskKt.validate([message1, message2], [])

        then:
        KlutterException e = thrown()
        CoreTestUtil.verify(e.message,
                """Processing annotation '@KlutterResponse' failed, caused by:
            
                            Could not resolve the following classes:
                            
                            - 'FartCannon'
                            
                            
                            Verify if all KlutterResponse annotated classes comply with the following rules:
                            
                            1. Must be an open class
                            2. Fields must be immutable
                            3. Constructor only (no body)
                            4. No inheritance
                            5. Any field type should comply with the same rules
                            
                            If this looks like a bug please file an issue at: https://github.com/buijs-dev/klutter/issues
                            """

        )

    }

    def "Validate no exception is thrown if customDataType list not empty after validating"() {
        when:
        def message1 = new DartMessage("Bob",
                [new DartField("FartCannon", "", false, false, true)]
        )

        def message2 = new DartMessage("Dave",
                [new DartField("String", "", false, false, false)]
        )

        def message3 = new DartMessage("FartCannon",
                [new DartField("GruMood", "intensity", false, false, true)]
        )

        def enum1 = new DartEnum("GruMood", ["BAD, GOOD, CARING, HATING, CONQUER_THE_WORLD"], [])

        then:
        AdapterGeneratorTaskKt.validate([message1, message2, message3], [enum1])

    }

}