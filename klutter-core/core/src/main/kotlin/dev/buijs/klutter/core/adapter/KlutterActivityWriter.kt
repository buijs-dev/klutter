package dev.buijs.klutter.core.adapter

import dev.buijs.klutter.core.KlutterCodeGenerationException
import java.io.File


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
internal class KlutterActivityWriter {

    fun write(file: File, classBody: String) {
        if(!file.exists()){
            throw KlutterCodeGenerationException(
                "The given path to the MainActivity file in the android/app folder does not exist.\r\n" +
                "Make sure the given path in the KlutterAdapterPlugin is an absolute path pointing " +
                "to the android/app folder in the flutter root project.")

        } else file.delete()

        file.createNewFile()
        file.writeText(classBody)
    }

}