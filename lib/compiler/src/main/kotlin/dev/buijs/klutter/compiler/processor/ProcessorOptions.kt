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

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.compiler.processor.ProcessorOption.*
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

/**
 * Parsed ksp options used by [Processor].
 */
data class ProcessorOptions(
    val metadataFolder: File,
    val outputFolder: File,
    val generateAdapters: Boolean,
    val isIntelBasedBuildMachine: Boolean,
)

/**
 * Possible options that can be set in the ksp DSL to configure
 * Klutter code scanning/generation.
 */
internal enum class ProcessorOption(val value: String) {
    METADATA_FOLDER("klutterScanFolder"),
    OUTPUT_FOLDER("klutterOutputFolder"),
    GENERATE_ADAPTERS("klutterGenerateAdapters"),
    INTEL_BASED_APPLE("intelMac"),
}

/**
 * Parse options set in the ksp DSL and return a [ProcessorOptions] object
 * or throw a [KlutterException] if required options are missing/invalid.
 */
internal fun SymbolProcessorEnvironment.processorOptions() = ProcessorOptions(
    metadataFolder = options.metadataFolder(),
    outputFolder = options.outputFolder(),
    generateAdapters = options.boolean(GENERATE_ADAPTERS),
    isIntelBasedBuildMachine = options.boolean(INTEL_BASED_APPLE),
)

/**
 * Parse required ksp option which contains path to the build directory.
 */
private fun Map<String,String>.metadataFolder(): File {
    val option = METADATA_FOLDER.value
    val pathToScanFolder = this[option]
        ?: throw KlutterException("""Option $option not set! 
                |Add this option to the ksp DSL, example: 
                |```
                |ksp {
                |    arg("$option", project.buildDir.absolutePath)
                |}
                |```
                |""".trimMargin())

    return File(pathToScanFolder)
        .also { it.verifyExists() }
        .resolve("klutter")
        .also { it.delete(); it.mkdir() }
}

/**
 * Parse required ksp option which contains path to the project output folder.
 */
private fun Map<String,String>.outputFolder(): File {
    val option = OUTPUT_FOLDER.value
    val pathToOutputFolder = this[option]
        ?: throw KlutterException("""Option $option not set!
                |Add this option to the ksp DSL, example:
                |```
                |ksp {
                |    arg("$option", project.buildDir.absolutePath)
                |}
                |```
                |""".trimMargin())
    return File(pathToOutputFolder).also { it.verifyExists() }
}

/**
 * Return option argument as boolean value or default to true if not set.
 */
private fun Map<String,String>.boolean(option: ProcessorOption) =
    this[option.value]?.let { it.trim().lowercase() == "true" } ?: true