package dev.buijs.klutter.core.adapter

import dev.buijs.klutter.core.KlutterCodeGenerationException
import java.io.File
import java.nio.file.Path


/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 * Takes the printed body and writes it to a file.
 */
internal class KlutterAndroidAdapterWriter {

    fun write(path: File, classBody: String) {
        if(!path.exists()){
            throw KlutterCodeGenerationException("""
                The given path to the android/app folder does not exist.
                Make sure the given path is an absolute path pointing to the
                android/app folder in the flutter root project.
            """.trimIndent())
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