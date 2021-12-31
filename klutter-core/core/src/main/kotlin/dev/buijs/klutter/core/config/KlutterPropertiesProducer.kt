package dev.buijs.klutter.core.config

import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterProducer
import java.nio.file.Path

class KlutterPropertiesProducer(
    private val path: Path,
    private val properties: List<YamlProperty>,
    private val logger: KlutterLogger
): KlutterProducer {

    override fun produce() {
        val file = path.resolve("klutter.properties").toAbsolutePath().toFile()

        if(file.exists()){
            logger.info("Deleting existing file: $file")
            file.delete()
        }

        file.createNewFile()
        logger.info("Created new file: $file")

        val writer = file.bufferedWriter()
        properties.forEach { property ->
            val key = property.key
            val value = property.value
            writer.append("$key=$value\r\n")
            logger.debug("Appending property '$key=$value' to file: $file")
        }
        writer.close()
    }
}