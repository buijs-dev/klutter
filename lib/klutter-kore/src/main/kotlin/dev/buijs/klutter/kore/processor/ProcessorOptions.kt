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
package dev.buijs.klutter.kore.processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.processor.ProcessorOption.*
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

/**
 * Parsed ksp options used by [Processor].
 */
data class ProcessorOptions(
    val outputFolder: File,
)

/**
 * Possible options that can be set in the ksp DSL to configure
 * Klutter code scanning/generation.
 */
internal enum class ProcessorOption(val value: String) {
    SCAN_FOLDER("klutterScanFolder")
}

/**
 * Parse options set in the ksp DSL and return a [ProcessorOptions] object
 * or throw a [KlutterException] if mandatory options are missing/invalid.
 */
internal fun SymbolProcessorEnvironment.processorOptions(): ProcessorOptions {
    val pathToOutputFolder = options[SCAN_FOLDER.value]
        ?: throw KlutterException("""Option ${SCAN_FOLDER.value} not set! 
                |Add this option to the ksp DSL, example: 
                |```
                |ksp {
                |    arg("${SCAN_FOLDER.value}", project.buildDir.absolutePath)
                |}
                |```
                |""".trimMargin())

    val outputFolder = File(pathToOutputFolder)
        .also { it.verifyExists() }
        .resolve("klutter")
        .also { it.delete(); it.mkdir() }

    return ProcessorOptions(
        outputFolder = outputFolder
    )
}