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
import dev.buijs.klutter.kore.shared.Method
import dev.buijs.klutter.kore.shared.maybePostfixToKJson

class IosAdapter(
    private val pluginClassName: String,
    private val methodChannelName: String,
    private val methods: List<Method>,
): KlutterPrinter {

    override fun print() = """
            |import Flutter
            |import UIKit
            |import Platform
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
            |${methods.asFunctionBodyString()}
            |        default:
            |            result(FlutterMethodNotImplemented)
            |        }
            |  }
            |
            |${methods.joinToString("\n\n") { it.print() }}  
            |}
            |""".trimMargin()

    private fun List<Method>.asFunctionBodyString() = joinToString("\n") {
        """ |       case "${it.command}":
            |            self.${it.command}(result: result)""".trimMargin()
    }

    private fun Method.print(): String {
        val method = method.replace("(context)", """(context: "")""")
        return if(async) {
            """|    func ${command}(result: @escaping FlutterResult) {
               |        ${method.removeSuffix("()")} { data, error in
               |            if let response = data { result(response${dataType.maybePostfixToKJson()}) }
               |                     
               |            if let failure = error { result(failure) }                       
               |        }   
               |    }                                      
            """.trimMargin()
        } else {
            """|    func ${command}(result: @escaping FlutterResult) {
               |        result(${method}${dataType.maybePostfixToKJson()})
               |    }
            """.trimMargin()
        }

    }
}