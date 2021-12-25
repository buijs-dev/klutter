package dev.buijs.klutter.core.adapter

import dev.buijs.klutter.core.KlutterCodeGenerationException
import java.io.File


/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 * Takes the printed body and writes it to a file.
 */
internal class KlutterActivityWriter {

    fun write(file: File, classBody: String) {
        if(!file.exists()){
            throw KlutterCodeGenerationException("""
                The given path to the MainActivity file in the android/app folder does not exist.
                Make sure the given path in the KlutterAdapterPlugin is an absolute path pointing
                to the android/app folder in the flutter root project.
            """.trimIndent())
        } else file.delete()

        file.createNewFile()
        file.writeText(classBody)
    }

}