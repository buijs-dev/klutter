package dev.buijs.klutter.core.flutter

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import dev.buijs.klutter.core.MethodCallDefinition
import java.io.File

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
internal class FlutterAdapterGenerator(
    private val flutter: Flutter,
    private val methods: List<MethodCallDefinition>
    ): KlutterFileGenerator() {

    override fun printer() = FlutterAdapterPrinter(methods)

    override fun writer() = FlutterAdapterWriter(flutter, printer().print())

}

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

    private fun printFun(definition: MethodCallDefinition): String {
        return """|    static Future<String?> get ${definition.getter} async {
                  |      return await _channel.invokeMethod('${definition.getter}');
                  |    }""".trimMargin()
    }

}

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