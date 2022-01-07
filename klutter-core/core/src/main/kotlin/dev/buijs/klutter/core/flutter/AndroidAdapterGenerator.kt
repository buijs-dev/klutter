package dev.buijs.klutter.core.flutter

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import java.io.File
import java.nio.file.Path

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
internal class AndroidAdapterGenerator(
    private val definitions: List<MethodCallDefinition>,
    private val path: File
): KlutterFileGenerator() {

    override fun printer() = AndroidAdapterPrinter(definitions)

    override fun writer() = AndroidAdapterWriter(path, printer().print())

}

internal class AndroidAdapterPrinter(
    private val definitions: List<MethodCallDefinition>
    ): KlutterPrinter
{

    override fun print(): String {

        val block = if (definitions.isEmpty()) {
            "return result.notImplemented()"
        } else {
            val defs = definitions.joinToString("else") { printFun(it) }
            "$defs else result.notImplemented()"
        }

        val imports = definitions
            .map { it.import }
            .distinct()
            .joinToString("\r\n") { "import $it" }

        return """
            |package dev.buijs.klutter.adapter
            |
            |$imports
            |import io.flutter.plugin.common.MethodChannel
            |import io.flutter.plugin.common.MethodChannel.Result
            |import io.flutter.plugin.common.MethodCall
            |
            |/**
            | * Generated code by the Klutter Framework
            | */
            |class GeneratedKlutterAdapter {
            |
            |  fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
            |       $block
            |  }
            |
            |}
            """.trimMargin()
    }

    private fun printFun(definition: MethodCallDefinition): String {
        return """
            | if (call.method == "${definition.getter}") {
            |            result.success(${definition.call})
            |        } """.trimMargin()
    }

}

internal class AndroidAdapterWriter(
    private val path: File,
    private val classBody: String)
    : KlutterWriter
{

    override fun write(): KlutterLogger {
        val logger = KlutterLogger()

        if(!path.exists()){
            throw KlutterCodeGenerationException("The given path to the android/app folder does not exist.\r\n"+
                    "Make sure the given path is an absolute path pointing to the android/app folder in the flutter root project.")
        }

        val androidClassPath = path.resolve(
            Path.of("src", "main", "java", "dev", "buijs", "klutter", "adapter").toFile())

        if(!androidClassPath.exists()) {
            androidClassPath.mkdirs()
        }

        val classFile =
            androidClassPath.resolve( "GeneratedKlutterAdapter.kt")

        if(classFile.exists()) {
            classFile.delete()
        }

        classFile.createNewFile()

        if(!classFile.exists()){
            throw KlutterCodeGenerationException("""
                Unable to create folder in the given path $path.
            """.trimIndent())
        }

        classFile.writeText(classBody)
        return logger
    }
}