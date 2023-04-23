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
package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.kore.shared.*
import dev.buijs.klutter.kore.templates.*
import dev.buijs.klutter.kore.templates.flutter.*

/**
 * Task to generate the boilerplate code required to
 * let Kotlin Multiplatform and Flutter communicate.
 */
class GenerateAdaptersForPluginTask(
    private val android: Android,
    private val ios: IOS,
    private val root: Root,
    private val methodChannelName: String,
    private val pluginName: String,
    private val excludeArmArcFromPodspec: Boolean,
    private val controllers: List<Controller>,
    private val metadata: List<SquintMessageSource>,
    private val log: (String) -> Unit = {  },
) : KlutterTask {

    override fun run() {

        val folder = root.pathToLibFolder.also {
            it.deleteRecursively()
            it.mkdir()
        }

        val srcFolder = folder.resolve("src")
            .also { it.mkdir() }
            .verifyExists()

        "flutter pub get".execute(root.folder)

        metadata.forEach { message ->
            var command = "flutter pub run squint_json:generate" +
                    " --type dataclass" +
                    " --input ${message.source}" +
                    " --output ${srcFolder.absolutePath}" +
                    " --overwrite true" +
                    " --generateChildClasses false" +
                    " --includeCustomTypeImports true"
            command.execute(root.folder).also { log(it) }

            // Serializer code does not need to be generated for enumerations.
            if(message.type is CustomType) {
                command = "flutter pub run squint_json:generate" +
                        " --type serializer" +
                        " --input ${srcFolder.resolve("${message.type.className.toSnakeCase()}_dataclass.dart")}" +
                        " --output ${srcFolder.absolutePath}" +
                        " --overwrite true"
                command.execute(root.folder).also { log(it) }
            }
        }

        val eventChannelNames = mutableSetOf<String>()
        val methodChannelNames = mutableSetOf<String>()

        controllers.filterIsInstance<BroadcastController>().forEach { controller ->
            srcFolder
                .resolve(controller.className.toSnakeCase())
                .also { if(!it.exists()) it.mkdir() }
                .resolve("controller.dart")
                .maybeCreate()
                .write(
                    SubscriberWidget(
                        topic = controller.className.toSnakeCase(),
                        channel = "$methodChannelName/channel/${controller.className.toSnakeCase()}"
                            .also { eventChannelNames.add(it) },
                        controllerName = controller.className,
                        dataType = controller.response
                    )
                )
        }

        controllers.filterIsInstance<RequestScopedController>().forEach { controller ->
            val parentFolder = srcFolder.resolve(controller.className.toSnakeCase()).also {
                if(!it.exists()) it.mkdir()
            }
            controller.functions.forEach { function ->
                parentFolder.resolve("${function.method.toSnakeCase()}.dart")
                    .also { it.createNewFile() }
                    .write(
                        PublisherWidget(
                            channel = FlutterChannel("$methodChannelName/channel/${controller.className.toSnakeCase()}")
                                .also { methodChannelNames.add(it.name) },
                            event = FlutterEvent(function.command),
                            extension = FlutterExtension("${function.command.replaceFirstChar { it.uppercase()}}Event"),
                            requestType = function.requestDataType?.let { FlutterMessageType(it) },
                            responseType = FlutterMessageType(function.responseDataType),
                            method = FlutterMethod(function.method),
                        ).createPrinter()
                    )
            }
        }

        root.pathToLibFile.maybeCreate().write(
            PackageLib(
                name = pluginName,
                exports = srcFolder.walkTopDown()
                    .toList()
                    .filter { it.isFile }
                    .map { it.relativeTo(srcFolder) }
                    .map { it.path.prefixIfNot("src/") }
            )
        )

        "dart format .".execute(root.folder).also { log(it) }

        if(excludeArmArcFromPodspec) {
            ios.podspec().excludeArm64("dependency'Flutter'")
        }

        ios.pathToPlugin.maybeCreate().write(
            IosAdapter(
                pluginClassName = ios.pluginClassName,
                methodChannels = methodChannelNames,
                eventChannels = eventChannelNames,
                controllers = controllers.toSet(),
            )
        )

        android.pathToPlugin.maybeCreate().write(
            AndroidAdapter(
                pluginClassName = android.pluginClassName,
                pluginPackageName = android.pluginPackageName,
                methodChannels = methodChannelNames,
                eventChannels = eventChannelNames,
                controllers = controllers.toSet(),
            )
        )

    }

}