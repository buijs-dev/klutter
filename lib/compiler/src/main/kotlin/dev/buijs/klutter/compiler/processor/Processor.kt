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
import dev.buijs.klutter.compiler.scanner.scanForControllers
import dev.buijs.klutter.compiler.scanner.scanForResponses
import dev.buijs.klutter.compiler.validator.*
import dev.buijs.klutter.compiler.validator.Invalid
import dev.buijs.klutter.compiler.validator.InvalidSquintMessages
import dev.buijs.klutter.compiler.validator.Valid
import dev.buijs.klutter.compiler.validator.ValidSquintMessages
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.ast.SquintMessageSource
import dev.buijs.klutter.kore.project.Project
import dev.buijs.klutter.kore.project.Pubspec
import dev.buijs.klutter.kore.project.plugin
import dev.buijs.klutter.kore.project.toPubspec
import dev.buijs.klutter.tasks.codegen.GenerateCodeOptions
import dev.buijs.klutter.tasks.codegen.GenerateCodeTask
import dev.buijs.klutter.tasks.codegen.findGenerateCodeAction
import java.io.File

/**
 * The actual symbol processor which will scan all classes with Klutter annotations
 * and run code generation tasks as configured.
 */
class Processor(
    private val options: ProcessorOptions,
    private val logger: KSPLogger,
): SymbolProcessor {

    private lateinit var output: File
    private lateinit var project: Project
    private lateinit var pubspec: Pubspec

    private var messages: List<SquintMessageSource>? = null
    private var controllers: List<Controller>? = null

    override fun process(resolver: Resolver): List<KSAnnotated> {
        output = options.outputFolder
        project = output.plugin()
        pubspec = project.root.toPubspec()
        resolver.findAndValidateMessages()
        resolver.findAndValidateControllers()
        generateCodeAndInstall()
        return emptyList()
    }

    private fun Resolver.findAndValidateMessages() {
        messages = scanForResponses(resolver = this, outputFolder = options.metadataFolder)
            .validateResponses().let {
                when (it) {
                    is ValidSquintMessages -> it.data
                    is InvalidSquintMessages -> {
                        logger.logAnnotationScanningWarnings(it.data, "@Response")
                        null
                    }
                }
            }
    }

    private fun Resolver.findAndValidateControllers() {
        controllers = scanForControllers(resolver = this, outputFolder = options.metadataFolder)
            .validateControllers(messages?.map { it.type } ?: emptyList()).let {
                when (it) {
                    is Valid -> it.data
                    is Invalid -> {
                        logger.logAnnotationScanningWarnings(it.data, "@Controller")
                        null
                    }
                }
            }
    }

    private fun generateCodeAndInstall() {
        // If null then there are validation errors and code generation should be skipped
        if (messages == null || controllers == null) return

        logger.logSquintInfo(project, messages!!.size, controllers!!.size)

        val codegenOptions = GenerateCodeOptions(
            project = project,
            pubspec = pubspec,
            excludeArmArcFromPodspec = options.isIntelBasedBuildMachine,
            controllers = controllers!!,
            messages = messages!!,
            log = { str -> logger.info("Running dart command:\n$str") })

        findGenerateCodeAction<GenerateCodeTask>(codegenOptions).run()
    }
}

private fun KSPLogger.logAnnotationScanningWarnings(warnings: List<String>, annotation: String) {
    warn("Fatal error occured while processing $annotation classes")
    warn("=============================================================")
    warnings.forEach { warn(it) }
    warn("=============================================================")
}

private fun KSPLogger.logSquintInfo(project: Project, messageCount: Int, controllerCount: Int) {
    info("=============================================================")
    info("Generating dart code")
    info("Root folder: ${project.root.folder.absolutePath}")
    info("Response count: $messageCount")
    info("Controller count: $controllerCount")
    info("=============================================================")
}