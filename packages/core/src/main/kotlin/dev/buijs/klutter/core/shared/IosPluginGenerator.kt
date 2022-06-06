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

package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.DartKotlinMap
import dev.buijs.klutter.core.KlutterFileGenerator
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.Method
import dev.buijs.klutter.core.FileWriter
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class IosPluginGenerator(
    private val path: File,
    private val frameworkName: String = "platform",
    private val methodChannelName: String,
    private val pluginClassName: String,
    private val methods: List<Method>,
): KlutterFileGenerator() {

    override fun printer() = IosPluginPrinter(
        frameworkName = frameworkName,
        pluginClassName = pluginClassName,
        methodChannelName = methodChannelName,
        methods = methods,
    )

    override fun writer() = FileWriter(path, printer().print())

}

internal class IosPluginPrinter(
    private val frameworkName: String,
    private val pluginClassName: String,
    private val methodChannelName: String,
    private val methods: List<Method>,
): KlutterPrinter {

    override fun print() = """
            |import Flutter
            |import UIKit
            |import $frameworkName
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
            |
            |${methods()}  
            |}
            |""".trimMargin()

    private fun blocks() = methods.joinToString("\n") {
        "        case \"${it.command}\":\n            self.${it.command}(result: result)"
    }

    private fun methods(): String = methods.joinToString("\n\n") { printMethod(it) }

    private fun printMethod(definition: Method): String {

        val type = if (DartKotlinMap.toMapOrNull(definition.dataType) == null) {
            ".toKJson()"
        } else ""

        return if(definition.async) {
            "    func ${definition.command}(result: @escaping FlutterResult) {\n" +
                    "        ${definition.method.removeSuffix("()")} { data, error in\n" +
                    "\n" +
                    "            if let response = data { result(response$type) }\n" +
                    "\n" +
                    "            if let failure = error { result(failure) }\n" +
                    "\n" +
                    "        }\n" +
                    "    }\n"
        } else {
            "    func ${definition.command}(result: @escaping FlutterResult) {\n" +
                    "        result(${definition.method}$type)\n" +
                    "    }"
        }

    }
}