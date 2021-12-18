package dev.buijs.klutter.core.adapter

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
class KlutterFlutterAdapterPrinterTest : WordSpec({

    "Using the KlutterAdapterPrinter" should {
        "Create the body GeneratedAdapter body with a branch for each KlutterAdaptee annotation" {
            val sut = KlutterFlutterAdapterPrinter()

            val definitions = listOf(
                MethodCallDefinition(
                    call = "FooBar().zeta()",
                    getter = "doFooBar",
                    import = "io.foo.bar.FooBar",
                    returns = Any::class.java),
                MethodCallDefinition(
                    call = "FooBar().beta()",
                    getter = "notDoFooBar",
                    import = "io.foo.bar.FooBar",
                    returns = Any::class.java))

            val actual = sut.print(definitions)

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