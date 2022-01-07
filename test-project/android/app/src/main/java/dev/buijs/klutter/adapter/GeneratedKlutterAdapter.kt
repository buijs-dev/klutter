package dev.buijs.klutter.adapter

import dev.buijs.klutter.example.basic.backend.Greeting
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall

/**
 * Generated code by the Klutter Framework
 */
class GeneratedKlutterAdapter {

  fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "getGreeting") {
            result.success(Greeting().greeting())
        }  else result.notImplemented()
  }

}