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

package dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter


import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import dev.buijs.klutter.core.MethodCallDefinition
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class FlutterAdapterGenerator(
    private val flutter: Flutter,
    private val methods: List<MethodCallDefinition>
    ): KlutterFileGenerator() {

    override fun printer() = FlutterAdapterPrinter(methods)

    override fun writer() = FlutterAdapterWriter(flutter, printer().print())

}

/**
 * @author Gillian Buijs
 */
internal class FlutterAdapterPrinter(
    private val definitions: List<MethodCallDefinition>
    ): KlutterPrinter {

    override fun print(): String {

        val block = definitions.joinToString("\r\n\r\n") { printFun(it) }

        return """
            |import 'dart:async';
            |import 'package:flutter/services.dart';
            |
            |class Adapter {
            |   static const MethodChannel _channel = MethodChannel('KLUTTER');
            |
            |$block
            |
            |}
            """.trimMargin()
    }

    private fun printFun(definition: MethodCallDefinition) =
        """|    static Future<String?> get ${definition.getter} async {
           |      return await _channel.invokeMethod('${definition.getter}');
           |    }""".trimMargin()

}

/**
 * @author Gillian Buijs
 */
internal class FlutterAdapterWriter(
    private val path: Flutter,
    private val classBody: String)
    : KlutterWriter {

    private val logger = KlutterLogger()

    override fun write(): KlutterLogger {

        val libFolder = path.file

        val generatedFolder = libFolder.resolve("generated").also {
            if(!it.exists()) it.mkdir()
        }

        val classFile = generatedFolder.resolve( "adapter.dart").also { file ->
            if(file.exists()) {
                file.delete()
                logger.info("Deleted existing file: $file")
            }
        }

        classFile.createNewFile().also { exists ->
            if(!exists){
                throw KlutterCodeGenerationException("Unable to create adapter file in the given path $path")
            } else logger.info("Created new file: $classFile")
        }

        val dartFile = findMainDartFile(libFolder)

        if(!dartFile.exists()){
            throw KlutterCodeGenerationException("File does not exist: $dartFile")
        }

        val mainLines = dartFile.readLines()

        val hasAdapterImport = mainLines.any {
            it.contains("import 'generated/adapter.dart'")
        }

        val mainBody = if(hasAdapterImport) {
            mainLines.joinToString("\r\n")
        } else {
            logger.debug("Added import to main.dart file: $dartFile")
            "import 'generated/adapter.dart';\r\n" + mainLines.joinToString("\r\n")
        }

        dartFile.writeText(mainBody).also {
            logger.debug("Written content to file $dartFile:\r\n$mainBody")
        }

        classFile.writeText(classBody).also {
            logger.debug("Written content to file $classFile:\r\n$classBody")
        }

        return logger
    }

    private fun findMainDartFile(directory: File): File {
        logger.debug("Scanning for main.dart in directory '$directory'")
        if (directory.exists()) {
            directory.walkTopDown().forEach { f ->
                logger.debug("Found file '$f' with name ${f.name} and extenions ${f.extension}")
                if(f.isFile && f.name == "main.dart"){
                    logger.debug("Found main.dart file in directory '$f''")
                    return f
                }
            }
            throw KlutterCodeGenerationException("Could not find main.dart in directory: '$directory'")
        }
        throw KlutterCodeGenerationException("Could not find main.dart because directory does not exist: '$directory'")
    }
}