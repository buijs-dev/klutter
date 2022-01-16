package dev.buijs.klutter.gradle.tasks.adapter.flutter

import dev.buijs.klutter.core.MethodCallDefinition
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.FlutterAdapterPrinter
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
class FlutterAdapterPrinterTest : WordSpec({

    "Using the KlutterAdapterPrinter" should {
        "Create the body GeneratedAdapter body with a branch for each KlutterAdaptee annotation" {

            val definitions = listOf(
                MethodCallDefinition(
                    call = "FooBar().zeta()",
                    getter = "doFooBar",
                    import = "io.foo.bar.FooBar",
                    returns = "String"),
                MethodCallDefinition(
                    call = "FooBar().beta()",
                    getter = "notDoFooBar",
                    import = "io.foo.bar.FooBar",
                    returns = "String")
            )

            val actual = FlutterAdapterPrinter(definitions).print()

            actual.filter { !it.isWhitespace() } shouldBe """
                import 'dart:async';
                import 'package:flutter/services.dart';
                
                class Adapter {
                   static const MethodChannel _channel = MethodChannel('KLUTTER');
                
                    static Future<String?> get doFooBar async {
                         return await _channel.invokeMethod('doFooBar');
                    }
                
                    static Future<String?> get notDoFooBar async {
                         return await _channel.invokeMethod('notDoFooBar');
                    }
                }

                """.filter { !it.isWhitespace() }
        }
    }
})