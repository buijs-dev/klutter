package dev.buijs.klutter.core.config


import dev.buijs.klutter.core.KlutterFileGenerator
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import java.nio.file.Path

class KlutterPropertiesGenerator(
    private val path: Path,
    private val properties: List<YamlProperty>,
): KlutterFileGenerator() {

    override fun printer() = KlutterPropertiesPrinter(properties)

    override fun writer() = KlutterPropertiesWriter(path, printer().print())

}

class KlutterPropertiesPrinter(
    private val properties: List<YamlProperty>)
    : KlutterPrinter
{

    override fun print(): String {
        val sb = StringBuilder()
        properties.forEach { property ->
            val key = property.key
            val value = property.value
            sb.append("$key=$value\r\n")
        }
        return sb.toString()
    }

}


class KlutterPropertiesWriter(
    val path: Path,
    val content: String)
    : KlutterWriter
{

    override fun write(): KlutterLogger {
        val logger = KlutterLogger()

        val file = path.resolve("klutter.properties").toAbsolutePath().toFile()

        if(file.exists()){
            logger.info("Deleting existing file: $file")
            file.delete()
        }

        file.createNewFile()
        logger.info("Created new file: $file")
        file.writeText(content)
        return logger
    }

}