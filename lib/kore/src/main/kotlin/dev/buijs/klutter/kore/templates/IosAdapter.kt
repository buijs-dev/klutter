/* Copyright (c) 2021 - 2023 Buijs Software
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

package dev.buijs.klutter.kore.templates

import dev.buijs.klutter.kore.*
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.Method
import dev.buijs.klutter.kore.common.toSnakeCase

class IosAdapter(
    private val pluginClassName: String,
    methodChannels: Set<String>,
    eventChannels: Set<String>,
    controllers: Set<Controller>,
): KlutterPrinter {

    private val imports = setOf(
        "import Flutter",
        "import UIKit",
        "import Platform",
        "import FlutterEngine"
    )

    private val methodChannelNames =
        methodChannels.map { """      "$it", """ }

    private val eventChannelNames =
        eventChannels.map { """      "$it", """ }

    private val methodChannelHandlerSwitchClauses = controllers
        .flatMap { it.functions }
        .map { listOf(
            """        case "${it.command}":""",
            """            self.${it.command}(data: data, result: result)""")
        }
        .flatten()

    private val methodChannelHandlerFunctions = controllers
        .filter { it.functions.isNotEmpty() }
        .flatMap { it.functions.flatMap { func -> func.methodHandlerString(it.instanceOrConstructor()) } }

    private val singletonControllerVariables = controllers
        .filter { it is Singleton }
        .map { it.className }
        .map { """    private let ${it.replaceFirstChar { char -> char.lowercase() }}: $it = $it()""" }
        .toSet()

    private val broadcastControllerReceivers = controllers
        .filterIsInstance<BroadcastController>()
        .flatMap { listOf(
            """     case "${it.className.toSnakeCase()}":""",
            "           ${it.instanceOrConstructor()}.receiveBroadcastIOS().collect(",
            "                  onEach: { value in",
            "                            eventSink(value${it.response.responseDecoderOrEmpty()})",
            "                        },",
            "                  onCompletion: { error in",
            """                             eventSink("ERROR: \("error")")""",
            "                        }",
            "                  )",
            "         return nil"
           )
         }

    override fun print(): String = buildString {
        appendLines(imports)
        appendLine()
        appendLine("public class $pluginClassName: NSObject, FlutterPlugin, FlutterStreamHandler {")
        appendLine()
        appendLine("    static let mcs: [String] = [")
        appendLines(methodChannelNames)
        appendLine("    ]")
        appendLine()
        appendLine("    static let ecs: [String] = [")
        appendLines(eventChannelNames)
        appendLine("    ]")
        appendLine()
        appendLine("    var ecFacade: EventChannelFacade!")
        appendLine("    var methodChannels: Set<FlutterMethodChannel> = []")
        appendLine()
        appendLines(singletonControllerVariables)
        appendLine()
        appendTemplate(
            """
                |    public static func register(with registrar: FlutterPluginRegistrar) {
                |        let messenger = registrar.messenger()
                |        let instance = $pluginClassName()
                |        
                |        for name in mcs {
                |           let channel = FlutterMethodChannel(name: name, binaryMessenger: messenger)
                |            instance.methodChannels.insert(channel)
                |            registrar.addMethodCallDelegate(instance, channel: channel)
                |        }
                |        
                |        instance.ecFacade = EventChannelFacade(
                |            handler: instance,
                |            channels: Set(ecs.map { FlutterEventChannel(name: ${'$'}0, binaryMessenger: messenger) }) )
                |   }
                |       
                |""")
        appendLine("    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {")
        appendLine("        let data = call.arguments")
        appendLine("        switch call.method {")
        appendLines(methodChannelHandlerSwitchClauses)
        appendLine("         default:")
        appendLine("            result(FlutterMethodNotImplemented)")
        appendLine("         }")
        appendLine("     }")
        appendLine()
        appendLines(methodChannelHandlerFunctions)
        appendTemplate("""
            |    public func onListen(withArguments: Any?, eventSink: @escaping FlutterEventSink) -> FlutterError? {
            |        let topic = withArguments ?? "none"
            |        switch "\(topic)" {
           """)
        appendLines(broadcastControllerReceivers)
        appendTemplate("""|          case "none":
            |           eventSink(FlutterError(code: "ERROR_CODE",
            |                                         message: "Topic not provided!",
            |                                         details: ""))
            |        default:
            |           eventSink(FlutterError(code: "ERROR_CODE",
            |                               message: "Unknown topic",
            |                               details: "\(withArguments)"))
            |        }
            |        return nil
            |     }
            |
            |    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
            |                ecFacade.cancel()
            |                return nil
            |    }
            |
            |}
        """)
    }

    private fun Method.methodHandlerString(instanceOrConstuctor: String): List<String> {
        val method = method.replace("(context)", """(context: "")""")

        val responseDecoderOrEmpty =
            responseDataType.responseDecoderOrEmpty()

        if(this.requestDataType != null) {
            return methodHandlerWithArgument(instanceOrConstuctor, responseDecoderOrEmpty)
        }

        return if(async) {
            listOf(
                """|    func ${command}(data: Any?, result: @escaping FlutterResult) {
                   |        $instanceOrConstuctor.${method.removeSuffix("()")} { maybeData, error in
                   |            if let response = maybeData { result(response$responseDecoderOrEmpty) }
                   |
                   |            if let failure = error { result(failure) }
                   |        }
                   |    }
                   |    
                """.trimMargin())
        } else {
            listOf(
                """|    func ${command}(data: Any?, result: @escaping FlutterResult) {
                   |        result($instanceOrConstuctor.$method()$responseDecoderOrEmpty)
                   |    }
                   |    
                """.trimMargin())
        }
    }

    private fun Method.methodHandlerWithArgument(instanceOrConstuctor: String, responseDecoderOrEmpty: String): List<String> {
        val requestDecoder = when(requestDataType) {
            is StringType -> "stringOrNull"
            is IntType -> "intOrNull"
            is DoubleType -> "doubleOrNull"
            is BooleanType -> "booleanOrNull"
            is ByteArrayType -> "byteArrayOrNull"
            is IntArrayType -> "intArrayOrNull"
            is LongArrayType -> "longArrayOrNull"
            is FloatArrayType -> "floatArrayOrNull"
            is DoubleArrayType -> "doubleArrayOrNull"
            is ListType -> "listOrNull"
            is MapType -> "mapOrNull"
            else -> ""
        }

        val lines = mutableListOf(
            "    func ${command}(data: Any?, result: @escaping FlutterResult) {",
            "        let dataOrNull: String? = TypeHandlerKt.$requestDecoder(data: data)",
            "        if(dataOrNull == nil) {",
            "           result(FlutterError(code: \"ERROR_CODE\",",
            "                           message: \"Expected ${requestDataType?.className} but got \\(dataOrNull)\",",
            "                           details: nil))",
            "        } else {")

        if(responseDataType is UnitType) {
            lines.add("           $instanceOrConstuctor.$method($requestParameterName: dataOrNull!)$responseDecoderOrEmpty")
            if(requestDataType is UnitType) {
                lines.add("""           result("")""")
            } else {
                lines.add("           result(dataOrNull!)")
            }
        } else {
            lines.add("           result($instanceOrConstuctor.$method($requestParameterName: dataOrNull!)$responseDecoderOrEmpty)")
        }

        lines.add("     }")
        lines.add("}")


        return lines
    }

    private fun Controller.instanceOrConstructor() = when(this) {
        is Singleton -> className.replaceFirstChar { char -> char.lowercase() }
        else -> "${className}()"
    }

    private fun AbstractType.responseDecoderOrEmpty(alwaysNullable: Boolean = false): String {
        return when {
            this is StandardType -> ""
            this is Nullable -> "?.toKJson()"
            alwaysNullable -> "?.toKJson()"
            else -> ".toKJson()"
        }
    }


}
