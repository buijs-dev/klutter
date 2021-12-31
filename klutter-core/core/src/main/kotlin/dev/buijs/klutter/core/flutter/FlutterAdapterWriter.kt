package dev.buijs.klutter.core.flutter

import dev.buijs.klutter.core.KlutterCodeGenerationException
import java.io.File


/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 * Takes the printed body and writes it to a file.
 */
internal class FlutterAdapterWriter {

    fun write(path: File, classBody: String) {
        if(!path.exists()){
            throw KlutterCodeGenerationException(" The given path to the flutter root folder does not exist.\r\n"+
                "Make sure the given path is an absolute path pointing to the flutter root project.")
        }

        val classFile = path.resolve( "GeneratedKlutterAdapter.dart")

        if(classFile.exists()) {
            classFile.delete()
        }

        classFile.createNewFile()

        if(!classFile.exists()){
            throw KlutterCodeGenerationException("""
                Unable to create GeneratedKlutterAdapter file in the given path $path.
            """.trimIndent())
        }

        val main = path.resolve("main.dart").absoluteFile
        val mainLines = main.readLines()
        val hasAdapterImport = mainLines.any { it.contains("import 'GeneratedKlutterAdapter.dart'") }
        val mainBody = if(hasAdapterImport) {
            mainLines.joinToString("\r\n")
        } else "import 'GeneratedKlutterAdapter.dart';\r\n" + mainLines.joinToString("\r\n")

        main.writeText(mainBody)
        classFile.writeText(classBody)
    }
}