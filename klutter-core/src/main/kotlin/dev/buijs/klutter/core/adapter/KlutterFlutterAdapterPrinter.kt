package dev.buijs.klutter.core.adapter

/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
internal class KlutterFlutterAdapterPrinter {

    fun print(definitions: List<MethodCallDefinition>): String =
        printClass(definitions)

    private fun printFun(definition: MethodCallDefinition): String {
        return """|    static Future<String?> get ${definition.getter} async {
                  |      return await _channel.invokeMethod('${definition.getter}');
                  |    }""".trimMargin()
    }

    private fun printClass(definitions: List<MethodCallDefinition>): String {

        val block = definitions.joinToString("\r\n\r\n") { printFun(it) }

        return """
            |import 'dart:async';
            |import 'package:flutter/services.dart';
            |
            |class Adapter {
            |   static const MethodChannel _channel = MethodChannel('KLUTTER');
            |
            |$block
            |
            |}
            """.trimMargin()
    }

}