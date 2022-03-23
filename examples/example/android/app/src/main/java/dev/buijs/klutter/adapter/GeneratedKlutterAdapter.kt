package dev.buijs.klutter.adapter

import com.example.example.Greeting
import com.example.example.ExtensiveGreeting
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Generated code by the Klutter Framework
 */
class GeneratedKlutterAdapter {

    private val mainScope = CoroutineScope(Dispatchers.Main)   
    
    fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
        mainScope.launch {
           when (call.method) {
                "getGreeting" -> {
                    result.success(Greeting().greeting())
                }
                "getExtensiveGreeting" -> {
                    result.success(ExtensiveGreeting().greeting().toKJson())
                } 
                else -> result.notImplemented()
           }
        }
    }
}