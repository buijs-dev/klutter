package com.example.example

import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.embedding.engine.FlutterEngine
import androidx.annotation.NonNull
import dev.buijs.klutter.adapter.GeneratedKlutterAdapter
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.android.FlutterActivity
import dev.buijs.klutter.annotations.kmp.KlutterAdapter

@KlutterAdapter
class MainActivity: FlutterActivity() {

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor,"KLUTTER")
            .setMethodCallHandler{ call, result ->
                GeneratedKlutterAdapter().handleMethodCalls(call, result)
            }
    }

}