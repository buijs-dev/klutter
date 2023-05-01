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
package dev.buijs.klutter.compiler.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.ast.SquintMessageSource
import dev.buijs.klutter.kore.project.plugin
import dev.buijs.klutter.kore.project.toPubspec
import dev.buijs.klutter.tasks.*

/**
 * The actual symbol processor which will scan all classes with Klutter annotations
 * and run code generation tasks as configured.
 */
class Processor(
    private val options: ProcessorOptions,
    private val logger: KSPLogger,
): SymbolProcessor {

    private val output = options.outputFolder
    private val project = output.plugin()
    private val pubspec = project.root.toPubspec()

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val input = options.metadataFolder

        val responses = resolver
            .annotatedWithResponse(input)
            .validateResponses()
            .validMessagesOrNull("@Response")

        val controllers = resolver
            .annotatedWithController(input)
            .validateControllers()
            .validControllersOrNull("@Controller")

        // If null then there are validation errors and code generation should be skipped
        if (responses == null || controllers == null)
            return emptyList()

        generateCodeAndInstall(controllers, responses)

        return emptyList()

    }

    private fun ValidationResultSquintMessages.validMessagesOrNull(
        annotation: String
    ): List<SquintMessageSource>? = when (this) {
        is ValidSquintMessages -> this.data
        is InvalidSquintMessages -> {
            logger.warn("Fatal error occured while processing $annotation classes")
            logger.warn("=============================================================")
            data.forEach { logger.warn(it) }
            logger.warn("=============================================================")
            null
        }
    }

    private fun ValidationResult.validControllersOrNull(
        annotation: String
    ): List<Controller>? = when (this) {
        is Valid -> this.data
        is Invalid -> {
            logger.warn("Fatal error occured while processing $annotation classes")
            logger.warn("=============================================================")
            data.forEach { logger.warn(it) }
            logger.warn("=============================================================")
            null
        }
    }

    private fun generateCodeAndInstall(
        controllers: List<Controller>,
        metadata: List<SquintMessageSource>
    ) {
        logger.info("=============================================================")
        logger.info("Generating dart code")
        logger.info("Root folder: ${project.root.folder.absolutePath}")
        logger.info("Response count: ${metadata.size}")
        logger.info("Controller count: ${controllers.size}")
        logger.info("=============================================================")
        val pluginName = pubspec.name ?: "klutter_library"
        val methodChannelName = pubspec.android?.pluginPackage ?: "$pluginName.klutter"
        GenerateAdaptersForPluginTask(
            android = project.android,
            ios = project.ios,
            root = project.root,
            controllers = controllers,
            metadata = metadata,
            excludeArmArcFromPodspec = options.isIntelBasedBuildMachine,
            methodChannelName = methodChannelName,
            pluginName = pluginName,
            log = { str -> logger.info("Running dart command:\n$str") }
        ).run()
    }
}