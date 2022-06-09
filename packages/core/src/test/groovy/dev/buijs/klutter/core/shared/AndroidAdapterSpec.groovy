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

import dev.buijs.klutter.core.Android
import dev.buijs.klutter.core.Method
import spock.lang.Shared
import spock.lang.Specification

import static dev.buijs.klutter.core.test.KlutterTest.plugin

class AndroidAdapterSpec extends Specification {

    @Shared
    def sut = new AndroidAdapter(
            new Android(new File("")),
            AdapterStub.get()
    )

    def "AndroidAdapter should create a valid Kotlin class"() {

        given:
        def plugin = plugin { project, resources ->
            def p = new File("${project.android.path}/src/main/kotlin/super_plugin")
            p.mkdirs()
            new File("${p.path}/SuperPlugin.kt").createNewFile()
        }

        def methods = [new Method(
                "greeting",
                "platform.Greeting",
                "Greeting().greeting()",
                false,
                "String"
        )]

        when:
        def android = new AndroidAdapter(
                new Android(plugin.android),
                new AdapterData(new PubspecData("super_plugin", new PubFlutter(
                        new Plugin(
                                new Platforms(
                                        new PluginClass("super_plugin", "SuperPlugin"),
                                        new PluginClass("super_plugin", "SuperPlugin"),)
                        )
                )), methods, [], [])
        )

        and:
        android.generate()

        then:
        with(android.path()) {adapter ->
            adapter.exists()
            adapter.text.replaceAll(" ", "") ==
                     """package super_plugin
            
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
                            channel = MethodChannel(flutterPluginBinding.binaryMessenger, "super_plugin")
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
                        """.replaceAll(" ", "")
        }

    }

    def "Verify toPath"(){
        expect:
        sut.toPath(input) == output

        where:
        input               | output
        null                | ""
        ""                  | ""
        "-1"                | "-1"
        "/foo/bar"          | "/foo/bar"
        "some.package.name" | "some/package/name"
    }
}