package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.MethodData
import dev.buijs.klutter.core.shared.AndroidPluginGenerator
import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class AndroidPluginGeneratorSpec extends Specification {

    def "AndroidPluginGenerator should create a valid Kotlin class"() {

        given:
        def adapter = Files.createTempFile("", "adapter.kt").toFile()
        def methodChannelName = "super_plugin"
        def pluginClassName = "SuperPlugin"
        def libraryPackage = ""
        def methods = [new MethodData(
                "greeting",
                "platform.Greeting",
                "Greeting().greeting()",
                false,
                "String"
        )]

        when:
        new AndroidPluginGenerator(
                adapter,
                methodChannelName,
                pluginClassName,
                libraryPackage,
                methods
        ).generate()

        then:
        adapter.exists()
        adapter.text.replaceAll(" ", "") ==
        """package 
            
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