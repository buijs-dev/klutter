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
package dev.buijs.klutter.tasks.codegen

import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.*
import dev.buijs.klutter.kore.templates.flutter.*
import java.io.File

fun GenerateCodeOptions.toGenerateControllersTask() =
    GenerateFlutterControllersTask(srcFolder = flutterSrcFolder, bindings = bindings)

/**
 * Generate the Flutter (dart) code in root/lib folder of the plugin project.
 */
class GenerateFlutterControllersTask(
    private val srcFolder: File,
    private val bindings: Map<String, Controller>,
) : KlutterTask, GenerateCodeAction {

    override fun run() {
        bindings.forEach { (channel, controller) ->
            when (controller) {
                is BroadcastController ->
                    srcFolder.writeBroadcastController(controller, channel)
                is SimpleController ->
                    srcFolder.writeSimpleController(controller, channel)
            }
        }
    }

}

private fun File.writeBroadcastController(
    controller: BroadcastController,
    channel: String
) {
    this.resolve(controller.className.toSnakeCase())
        .maybeCreateFolder()
        .resolve("controller.dart")
        .maybeCreate()
        .write(SubscriberWidget(
            topic = controller.className.toSnakeCase(),
            channel = channel,
            controllerName = controller.className,
            dataType = controller.response))
}

private fun File.writeSimpleController(
    controller: SimpleController,
    channel: String
) {
    val parentFolder =
        resolve(controller.className.toSnakeCase())
            .maybeCreateFolder()

    controller.functions.forEach { function ->
        parentFolder.resolve("${function.method.toSnakeCase()}.dart")
            .also { it.createNewFile() }
            .write(PublisherWidget(
                channel = FlutterChannel(channel),
                event = FlutterEvent(function.command),
                extension = FlutterExtension("${function.command.replaceFirstChar { it.uppercase() }}Event"),
                requestType = function.requestDataType?.let { FlutterMessageType(it) },
                responseType = FlutterMessageType(function.responseDataType),
                method = FlutterMethod(function.method)).createPrinter())
    }
}