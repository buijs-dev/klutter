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

package dev.buijs.klutter.kore.templates

import dev.buijs.klutter.kore.*
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.ast.ControllerData
import dev.buijs.klutter.kore.ast.Singleton
import dev.buijs.klutter.kore.shared.Method
import dev.buijs.klutter.kore.shared.maybePostfixToKJson

class IosAdapter(
    private val pluginClassName: String,
    methodChannels: Set<String>,
    eventChannels: Set<String>,
    controllers: Set<Controller>,
): KlutterPrinter {

    private val imports = setOf(
        "import Flutter",
        "import UIKit",
        "import Platform"
    )

    private val singletonControllerVariables = controllers
        .filter { it is Singleton }
        .map { it.className }
        .map { """private val ${it.replaceFirstChar { char -> char.lowercase() }}: $it = $it()""" }
        .toSet()

    override fun print(): String = buildString {
        appendLines(imports)
        appendLine()
        appendLine("public class Swift$pluginClassName: NSObject, FlutterPlugin, FlutterStreamHandler {")
        appendLine()
        appendLines(singletonControllerVariables)
        appendLine()
        appendLine("    public static func register(with registrar: FlutterPluginRegistrar) {")
    }

//    """
//
//
//            |
//            |        let channel = FlutterMethodChannel(name: "$methodChannelName", binaryMessenger: registrar.messenger())
//            |        let instance = Swift$pluginClassName()
//            ${controllers.printInstanceControllerSetter()}
//            |        registrar.addMethodCallDelegate(instance, channel: channel)
//            |        FlutterEventChannel(name: "$methodChannelName/stream", binaryMessenger: registrar.messenger())
//            |                .setStreamHandler(instance)
//            |  }
//            |
//            |  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
//            |    switch call.method {
//            ${methods.printFunctionBodies()}
//            |    default:
//            |         result(FlutterMethodNotImplemented)
//            |    }
//            |  }
//            ${methods.joinToString("\n\n") { it.print() }}
//            |    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
//            |
//            ${controllers.printBroadcastReceivers()}
//            |
//            |       return nil
//            |    }
//            |
//            |    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
//            ${controllers.printControllerCancellers()}
//            |        return nil
//            |    }
//            |}
//            |""".trimMargin()

}

internal fun ControllerData.lowercaseName() = this.name.lowercase()

private fun List<ControllerData>.printControllerInstance() =
    this.joinToString("\n") { "    private var ${it.lowercaseName()}: ${it.name}?" }

private fun List<ControllerData>.printInstanceControllerSetter() =
    this.joinToString("\n") { "    instance.${it.lowercaseName()} = ${it.name}()" }

private fun List<ControllerData>.printControllerCancellers() =
    this.joinToString("\n") { "        ${it.lowercaseName()}?.cancel()" }

private fun List<ControllerData>.printBroadcastReceivers() =
    this.joinToString("\n") { """
    |        ${it.lowercaseName()}?.receiveBroadcastIOS().collect(
    |            onEach: { value in
    |                events(value.toKJson())
    |            },
    |            onCompletion: { error in
    |                events("ERROR: \("error")")
    |            }
    |       )
    """.trimMargin() }

private fun List<Method>.printFunctionBodies() = joinToString("\n") {
//    """ |       case "${it.methodId}":
//            |            self.${it.command}(result: result)""".trimMargin()
    ""
}

private fun Method.print(): String {
    val method = method.replace("(context)", """(context: "")""")
//    return if(async) {
//        """|    func ${command}(result: @escaping FlutterResult) {
//               |        ${method.removeSuffix("()")} { data, error in
//               |            if let response = data { result(response${responseDataType.maybePostfixToKJson()}) }
//               |
//               |            if let failure = error { result(failure) }
//               |        }
//               |    }
//            """.trimMargin()
//    } else {
//        """|    func ${command}(result: @escaping FlutterResult) {
//               |        result(${method}${responseDataType.maybePostfixToKJson()})
//               |    }
//            """.trimMargin()
//    }
    return ""
}