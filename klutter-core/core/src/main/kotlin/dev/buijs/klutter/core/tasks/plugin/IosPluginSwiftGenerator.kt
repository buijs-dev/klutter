package dev.buijs.klutter.core.tasks.plugin

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import dev.buijs.klutter.core.tasks.shared.DefaultWriter
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class IosPluginSwiftGenerator(
    private val path: File,
    private val methodChannelName: String,
    private val pluginClassName: String,
    private val methods: List<MethodCallDefinition>,
): KlutterFileGenerator() {

    override fun printer() = IosPluginSwiftPrinter(
        pluginClassName = pluginClassName,
        methodChannelName = methodChannelName,
        methods = methods,
    )

    override fun writer() = DefaultWriter(path, printer().print())

}

internal class IosPluginSwiftPrinter(
    private val pluginClassName: String,
    private val methodChannelName: String,
    private val methods: List<MethodCallDefinition>,
): KlutterPrinter {

    override fun print() = """
            |import Flutter
            |import UIKit
            |import platform
            |
            |public class Swift$pluginClassName: NSObject, FlutterPlugin {
            |  public static func register(with registrar: FlutterPluginRegistrar) {
            |    let channel = FlutterMethodChannel(name: "$methodChannelName", binaryMessenger: registrar.messenger())
            |    let instance = Swift$pluginClassName()
            |    registrar.addMethodCallDelegate(instance, channel: channel)
            |  }
            |
            |  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
            |    switch call.method {
            |${blocks()}
            |        default:
            |            result(FlutterMethodNotImplemented)
            |        }
            |  }
            |}
            |
            |${methods()}  
            |""".trimMargin()

    private fun blocks() = methods.joinToString("\n") {
        "        case \"${it.getter}\":\n            self.${it.getter}(result: result)"
    }

    private fun methods(): String {
        return methods.joinToString("\n\n") { printMethod(it) }
    }

    private fun printMethod(definition: MethodCallDefinition): String {

        val type = if (DartKotlinMap.toMapOrNull(definition.returns) == null) {
            ".toKJson()"
        } else ""

        return if(definition.async) {
            "    func ${definition.getter}(result: @escaping FlutterResult) {\n" +
                    "        ${definition.call.removeSuffix("()")} { data, error in\n" +
                    "\n" +
                    "            if let response = data { result(response$type) }\n" +
                    "\n" +
                    "            if let failure = error { result(failure) }\n" +
                    "\n" +
                    "        }\n" +
                    "    }\n"
        } else {
            "    func ${definition.getter}(result: @escaping FlutterResult) {\n" +
                    "        result(${definition.call}$type)\n" +
                    "    }"
        }

    }

}