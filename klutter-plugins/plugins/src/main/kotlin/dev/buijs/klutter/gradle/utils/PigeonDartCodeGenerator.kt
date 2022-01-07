package dev.buijs.klutter.gradle.utils

import dev.buijs.klutter.core.Klutter
import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.gradle.dsl.KlutterServiceDTO
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.Root
import java.io.File

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class PigeonDartCodeGenerator(
    private val klutter: Klutter,
    private val serviceDTO: KlutterServiceDTO
) {

    private var logger = KlutterLogger()

    fun generate(): KlutterLogger {

        val target = klutter.file.resolve(".klutter").also {
            if(!it.exists()) it.mkdirs()
        }

        val pigeon = target.resolve("pigeon.dart").absoluteFile.also {
            if(it.exists()) it.delete()
        }

        pigeon.createNewFile()
        pigeon.writeText(PigeonDartClassPrinter().print(serviceDTO))
        return logger
    }


}