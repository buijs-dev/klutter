package dev.buijs.klutter.core.tasks.adapter.flutter

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import java.io.File
import java.nio.file.Path

/**
 * @author Gillian Buijs
 */
internal class AndroidAdapterGenerator(
    private val definitions: List<MethodData>,
    private val path: File
): KlutterFileGenerator() {

    override fun printer() = AndroidAdapterPrinter(definitions)

    override fun writer() = AndroidAdapterWriter(path, printer().print())

}

internal class AndroidAdapterPrinter(
    private val definitions: List<MethodData>
    ): KlutterPrinter
{

    override fun print(): String {

        val block = if (definitions.isEmpty()) {
            "             return result.notImplemented()"
        } else {
            val defs = definitions.joinToString("\r\n") { printFun(it) }
            "$defs \r\n                else -> result.notImplemented()"
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
            |import io.flutter.plugin.common.MethodCall
            |import kotlinx.coroutines.CoroutineScope
            |import kotlinx.coroutines.Dispatchers
            |import kotlinx.coroutines.launch
            |
            |/**
            | * Generated code by the Klutter Framework
            | */
            |class GeneratedKlutterAdapter {
            |
            |    private val mainScope = CoroutineScope(Dispatchers.Main)   
            |    
            |    fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
            |        mainScope.launch {
            |           when (call.method) {
            |$block
            |           }
            |        }
            |    }
            |}
            """.trimMargin()
    }

    private fun printFun(definition: MethodData): String {
        val type = if (DartKotlinMap.toMapOrNull(definition.returns) == null) {
            ".toKJson()"
        } else ""

        return  "                \"${definition.getter}\" -> {\r\n" +
                "                    result.success(${definition.call}${type})\r\n" +
                "                }"
    }

}

internal class AndroidAdapterWriter(
    private val path: File,
    private val classBody: String)
    : KlutterWriter
{

    override fun write() {

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

    }
}