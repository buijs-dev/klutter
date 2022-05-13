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

package dev.buijs.klutter.core.tasks.adapter.flutter


import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import dev.buijs.klutter.core.MethodCallDefinition
import dev.buijs.klutter.core.tasks.adapter.dart.EnumerationPrinter
import dev.buijs.klutter.core.tasks.adapter.dart.MessagePrinter
import dev.buijs.klutter.core.tasks.adapter.dart.getCastMethod
import org.gradle.api.logging.Logging
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class FlutterAdapterGenerator(
    private val flutter: Flutter,
    private val methods: List<MethodCallDefinition>,
    ): KlutterFileGenerator() {

    override fun printer() = FlutterAdapterPrinter(definitions = methods)

    override fun writer() = FlutterAdapterWriter(flutter, printer().print())

}

/**
 * @author Gillian Buijs
 */
internal class FlutterAdapterPrinter(
    private val methodChannelName: String = "KLUTTER",
    private val pluginClassName: String = "Adapter",
    private val definitions: List<MethodCallDefinition>,
    private val objects: DartObjects? = null,
    ): KlutterPrinter {

    override fun print(): String {

        val messages = objects?.messages?.joinToString("\n" + "\n") {
            MessagePrinter(it).print()
        }

        val enumerations = objects?.enumerations?.joinToString("\n" + "\n") {
            EnumerationPrinter(it).print()
        }

        val block = definitions.joinToString("\r\n\r\n") { printFun(it) }

        return """
            |import 'dart:convert';
            |import 'dart:async';
            |import 'messages.dart';
            |import 'package:flutter/services.dart';
            |
            |/// Autogenerated by Klutter Framework. 
            |/// 
            |/// Do net edit directly, but recommended to store in VCS.
            |/// 
            |/// Adapter class which handles communication with the KMP library.
            |class $pluginClassName {
            |  static const MethodChannel _channel = MethodChannel('$methodChannelName');
            |  
            $block
            |
            |}
            |
            |/// Autogenerated by Klutter Framework. 
            |/// 
            |/// Do net edit directly, but recommended to store in VCS.
            |/// 
            |/// Wraps an [exception] if calling the platform method has failed to be logged by the consumer.
            |/// Or wraps an [object] of type T when platform method has returned a response and
            |/// deserialization was successful.
            |class AdapterResponse<T> {
            |
            |  AdapterResponse(this._object, this._exception);
            |
            |  factory AdapterResponse.success(T t) => AdapterResponse(t, null);
            |
            |  factory AdapterResponse.failure(Exception e) => AdapterResponse(null, e);
            |  
            |  ///The actual object to returned
            |  T? _object;
            |  set object(T object) => _object = object;
            |  T get object => _object!;
            |
            |  ///Exception which occurred when calling a platform method failed.
            |  Exception? _exception;
            |  set exception(Exception e) => _exception = e;
            |  Exception get exception => _exception!;
            |
            |  bool isSuccess() {
            |    return _object != null;
            |  }
            |   
            |}
            |
            |${messages?:""}
            |
            |${enumerations?:""}
            |
            """.trimMargin()
    }

    private fun printFun(definition: MethodCallDefinition) =
        if(DartKotlinMap.toMapOrNull(definition.returns) == null) {
            """|  static Future<AdapterResponse<${definition.returns}>> get ${definition.getter} async {
           |    try {
           |      final response = await _channel.invokeMethod('${definition.getter}');
           |      final json = jsonDecode(response);
           |      return AdapterResponse.success(${serializer(definition)});
           |    } catch (e) {
           |      return AdapterResponse.failure(
           |          e is Error ? Exception(e.stackTrace) : e as Exception
           |      );
           |    }
           |  }"""
        } else {
            """|  static Future<AdapterResponse<${definition.returns}>> get ${definition.getter} async {
           |    try {
           |      final json = await _channel.invokeMethod('${definition.getter}');
           |      return AdapterResponse.success(${serializer(definition)});
           |    } catch (e) {
           |      return AdapterResponse.failure(
           |          e is Error ? Exception(e.stackTrace) : e as Exception
           |      );
           |    }
           |  }"""
        }


    private fun serializer(definition: MethodCallDefinition): String {

        val listRegex = """List<([^>]+?)>""".toRegex()

        var isList = false
        var type = definition.returns
        val q = if(type.contains("?")) "?" else ""

        listRegex.find(type)?.let {
            isList = true
            type = it.groups[1]?.value ?: type
        }

        val dartType = DartKotlinMap.toMapOrNull(type)?.dartType

        //Standard DART datatype
        if(dartType != null) {
            return if(isList) {
                "List<$type>.from(json.map((o) => o$q${getCastMethod(dartType)}))"
            } else "json${getCastMethod(dartType)}"
        }

        //Custom DTO or enum
        return if(isList) {
            "List<$type>.from(json.map((o) => $type.fromJson(o)))"
        } else "$type.fromJson(json)"

    }

}

/**
 * @author Gillian Buijs
 */
internal class FlutterAdapterWriter(
    private val path: Flutter,
    private val classBody: String)
    : KlutterWriter {

    private val log = Logging.getLogger(FlutterAdapterWriter::class.java)

    override fun write() {

        val libFolder = path.file

        val generatedFolder = libFolder.resolve("generated").also {
            if(!it.exists()) it.mkdir()
        }

        val classFile = generatedFolder.resolve( "adapter.dart").also { file ->
            if(file.exists()) {
                file.delete()
                log.info("Deleted existing file: $file")
            }
        }

        classFile.createNewFile().also { exists ->
            if(!exists){
                throw KlutterCodeGenerationException("Unable to create adapter file in the given path $path")
            } else log.info("Created new file: $classFile")
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
            log.debug("Added import to main.dart file: $dartFile")
            "import 'generated/adapter.dart';\r\n" + mainLines.joinToString("\r\n")
        }

        dartFile.writeText(mainBody).also {
            log.debug("Written content to file $dartFile:\r\n$mainBody")
        }

        classFile.writeText(classBody).also {
            log.debug("Written content to file $classFile:\r\n$classBody")
        }

    }

    private fun findMainDartFile(directory: File): File {
        log.debug("Scanning for main.dart in directory '$directory'")
        if (directory.exists()) {
            directory.walkTopDown().forEach { f ->
                log.debug("Found file '$f' with name ${f.name} and extenions ${f.extension}")
                if(f.isFile && f.name == "main.dart"){
                    log.debug("Found main.dart file in directory '$f''")
                    return f
                }
            }
            throw KlutterCodeGenerationException("Could not find main.dart in directory: '$directory'")
        }
        throw KlutterCodeGenerationException("Could not find main.dart because directory does not exist: '$directory'")
    }
}