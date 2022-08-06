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

package dev.buijs.klutter.core.templates

import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.shared.prefixIfNot

internal class KomposeIosAdapter(
    private val pluginClassName: String,
    private val methodChannelName: String,
    private val controllers: List<String> = emptyList(),
): KlutterPrinter {

    override fun print() = """
            |import Flutter
            |import UIKit
            |import Platform
            |
            |public class ${pluginClassName.prefixIfNot("Swift")}: NSObject, FlutterPlugin {
            |
            |    public static func register(with registrar: FlutterPluginRegistrar) {
            |        let channel = FlutterMethodChannel(name: "$methodChannelName", binaryMessenger: registrar.messenger())
            |        let instance = ${pluginClassName.prefixIfNot("Swift")}()
            |        registrar.addMethodCallDelegate(instance, channel: channel)
            |    }
            |
            |    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
            |
            |        switch call.method {
            |        case "kompose_event_trigger":
            |            // Parse the flutter call arguments to retrieve the event data.
            |            if let args = call.arguments as? Dictionary<String, Any>,
            |               let widget = args["widget"] as? String,
            |               let event = args["event"] as? String,
            |               let data = args["data"] as? String,
            |               let controllerName = args["controller"] as? String {
            |
            |                // Get a controller with the specified name.
            |                let controller = getController(controller: controllerName)
            |
            |                // If controller is nil then there is no controller
            |                // found with the given name. Return a FlutterError
            |                // to finish event processing.
            |                if (controller == nil) {
            |                    result(FlutterError.init(code: "Failed to process event", message: nil, details: nil))
            |                }
            |
            |                // Cast the controller to the correct controller implementation,
            |                // call the onEvent method and return the latest state as JSON string.
            |                else {
            |                    switch controller {
                                 ${controllers.serializeControllers()}
            |                    default: result(FlutterError.init(code: "invalid controller type!", message: nil, details: nil))
            |                    }
            |                }
            |            }
            |
            |            // The event data received from flutter is invalid.
            |            // Return a FlutterError to finish event processing.
            |            else {
            |                result(FlutterError.init(code: "invalid event data", message: nil, details: nil))
            |            }
            |
            |        case "kompose_dispose_controller":
            |            // Parse the flutter call arguments to retrieve the event data.
            |            if let args = call.arguments as? Dictionary<String, Any>,
            |               let controllerName = args["controller"] as? String {
            |                    // Try to dispose the controller.
            |                    // Will throw error if there is no such controller for the given name.
            |                    do {
            |                        try disposeController(controller: controllerName)
            |                        result("")
            |                    } catch {
            |                        result(FlutterError.init(code: "Failed to dispose controller", message: nil, details: nil))
            |                    }
            |            }
            |
            |            // The event data received from flutter is invalid.
            |            // Return a FlutterError to finish event processing.
            |            else {
            |                result(FlutterError.init(code: "invalid event data", message: nil, details: nil))
            |            }
            |
            |        case "kompose_init_controller":
            |            // Parse the flutter call arguments to retrieve the event data.
            |            if let args = call.arguments as? Dictionary<String, Any>,
            |               let controllerName = args["controller"] as? String {
            |                   let controller = getController(controller: controllerName)
            |
            |                    // If controller is nil then there is no controller
            |                    // found with the given name. Return a FlutterError
            |                    // to finish event processing.
            |                    if (controller == nil) {
            |                        result(FlutterError.init(code: "Failed to init controller", message: nil, details: nil))
            |                    } else {
            |                        switch controller {
                                     ${controllers.serializeControllerState()}
            |                        default: result(FlutterError.init(code: "invalid controller type!", message: nil, details: nil))
            |                    }
            |               }
            |            } 
            |            // The event data received from flutter is invalid.
            |            // Return a FlutterError to finish event processing.
            |            else {
            |                result(FlutterError.init(code: "invalid event data", message: nil, details: nil))
            |            }    
            |        default:
            |            result(FlutterMethodNotImplemented)
            |        }
            |    }
            |}
            |""".trimMargin()

    private fun List<String>.serializeControllers(): String {
        return joinToString("|\n") {
            """
            |                    case is ${it.withoutPackage()}:
            |                       let ${it.withoutPackage().lowercase()} = (controller as! ${it.withoutPackage()})
            |                       ${it.withoutPackage().lowercase()}.onEvent(event: event, data: data)
            |                       result(${it.withoutPackage().lowercase()}.serializeState() as String?)
            """
        }
    }

    private fun List<String>.serializeControllerState(): String {
        return joinToString("|\n") {
            """
            |                    case is ${it.withoutPackage()}:
            |                           let ${it.withoutPackage().lowercase()} = (controller as! ${it.withoutPackage()})
            |                           result(${it.withoutPackage().lowercase()}.serializeState() as String?)
            """
        }
    }

}