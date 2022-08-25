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

import dev.buijs.klutter.kore.templates.KomposeAndroidAdapter
import dev.buijs.klutter.kore.test.TestUtil
import spock.lang.Specification

class KomposeAndroidAdapterSpec extends Specification {

    def "KomposeAndroidAdapter should create a valid Kotlin class"() {
        given:
        def packageName = "super_plugin"
        def pluginName = "SuperPlugin"
        def channelName = "dev.company.plugins.super_plugins"

        and: "The printer as SUT"
        def adapter = new KomposeAndroidAdapter(packageName, pluginName, channelName)

        expect:
        TestUtil.verify(adapter.print(), classBody)

        where:
        classBody = """package super_plugin
        
        import androidx.annotation.NonNull
        import android.app.Activity
        import android.content.Context
        import io.flutter.embedding.engine.plugins.activity.ActivityAware
        import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        import io.flutter.embedding.engine.plugins.FlutterPlugin
        import io.flutter.plugin.common.MethodCall
        import io.flutter.plugin.common.MethodChannel
        import io.flutter.plugin.common.MethodChannel.MethodCallHandler
        import io.flutter.plugin.common.MethodChannel.Result
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        import kotlinx.coroutines.launch
        import java.io.ByteArrayInputStream
        import java.io.ObjectInputStream
        import java.util.*
        
        /** SuperPlugin */
        class SuperPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
          /// The MethodChannel that will the communication between Flutter and native Android
          ///
          /// This local reference serves to register the plugin with the Flutter Engine and unregister it
          /// when the Flutter Engine is detached from the Activity
          private lateinit var channel : MethodChannel
           
          private val mainScope = CoroutineScope(Dispatchers.Main) 
           
          private lateinit var context: Context
             
          private lateinit var activity: Activity
          
          override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
            context = flutterPluginBinding.applicationContext
            channel = MethodChannel(flutterPluginBinding.binaryMessenger, "dev.company.plugins.super_plugins")
            channel.setMethodCallHandler(this)
          }
        
          override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
                mainScope.launch {
                   when (call.method) {
                       "kompose_event_trigger" -> {
                           val widget: String? =  call.argument("widget")
                           val event: String? =  call.argument("event")
                           val data: String? =  call.argument("data")
                            val controllerName: String? = call.argument("controller")
                            val controller = getController(controllerName ?: "")
                           controller.onEvent(event ?: "", data ?: "")
                           result.success(controller.state.toKJson())
                       }
        
                       "kompose_dispose_controller" -> {
                           val controllerName: String? = call.argument("controller")
                           disposeController(controllerName ?: "")
                           result.success("")
                       }
                                   
                       "kompose_init_controller" -> {
                           val controllerName: String? = call.argument("controller")
                           val controller = getController(controllerName ?: "")
                           result.success(controller.state.toKJson())
                        }
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
        }
                        """
    }

}