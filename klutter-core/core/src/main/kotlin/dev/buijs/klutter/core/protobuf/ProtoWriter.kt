package dev.buijs.klutter.core.protobuf

import dev.buijs.klutter.core.Klutter
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterWriter

/**
 * @author Gillian Buijs
 */
class ProtoWriter(val content: String, val klutter: Klutter): KlutterWriter {

    override fun write(): KlutterLogger {
        val logger = KlutterLogger()

        val file = klutter.file.resolve(".klutter").also {
            if(!it.exists()){
                it.mkdir()
            }
        }

        val protoFile = file.resolve("klutter.proto").also {
            if(it.exists()) {
                it.delete().also {
                    logger.debug("Deleted proto file: $file")
                }
            } else logger.error("Proto file not found. Creating a new file!")
        }

        protoFile.createNewFile().also { logger.debug("Created new proto file: $file") }
        protoFile.writeText(content).also { logger.debug("Written content to $file:\r\n$content") }
        return logger
    }

}