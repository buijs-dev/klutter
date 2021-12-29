package dev.buijs.klutter.core.adapter.service

import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.log.KlutterLogger
import java.io.File

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
class PigeonDartCodeGenerator(
    private val flutter: File,
    private val serviceDTO: KlutterServiceDTO
) {

    private var logger = KlutterLogger()

    fun generate(): KlutterLogger {
        if(!flutter.exists()){
            throw KlutterCodeGenerationException("Flutter directory does not exist: ${flutter.absolutePath}")
        }

        val target = flutter.resolve("klutter/.klutter")

        if(!target.exists()) target.mkdirs()

        val pigeon = target.resolve("pigeon.dart").absoluteFile

        if(pigeon.exists()){
            pigeon.delete()
        }

        pigeon.createNewFile()
        pigeon.writeText(PigeonDartClassPrinter().print(serviceDTO))
        return logger
    }


}