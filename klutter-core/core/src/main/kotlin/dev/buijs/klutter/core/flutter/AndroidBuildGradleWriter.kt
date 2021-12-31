package dev.buijs.klutter.core.flutter

import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.KlutterWriter
import java.io.File

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
 * Takes the generated build.gradle content and writes it to the flutter/android/app folder.
 * If a build.gradle is found it is fully replaced by the newly generated content.
 *
 */
class AndroidBuildGradleWriter: KlutterWriter {

    override fun write(file: File, content: String) {
        if(!file.exists()){
            throw KlutterCodeGenerationException(
                "The given path to $file folder does not exist. " +
                "Make sure the given path in the KlutterAdapterPlugin is an absolute path pointing " +
                "to the android/app folder in the flutter root project.")
        } else file.delete()

        file.createNewFile()
        file.writeText(content)
    }

}