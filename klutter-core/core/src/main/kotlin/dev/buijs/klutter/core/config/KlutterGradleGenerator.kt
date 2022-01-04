package dev.buijs.klutter.core.config

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import java.nio.file.Path

class KlutterGradleFileGenerator(
    private val path: Path,
    private val properties: List<YamlProperty>
): KlutterFileGenerator() {

    override fun printer() = KlutterGradleFilePrinter(properties)

    override fun writer() = KlutterGradleFileWriter(path, printer().print())

}

internal fun dedotkey(dottie: String): String {
    var output = ""
    dottie.split(".").forEachIndexed { index, word ->
        output += if(index != 0) {
            word.replaceFirstChar { char -> char.uppercase() }
        } else word
    }
    return output
}

internal class KlutterGradleFilePrinter(
    private val properties: List<YamlProperty>)
    : KlutterPrinter
{

    override fun print(): String {
        val sb = StringBuilder()

        properties
            .sortedBy { it.key }
            .forEach { property ->
                val line = when(property.type) {
                    YamlPropertyType.Int -> {
                        formatAsInt(property.key, property.value)
                    }

                    YamlPropertyType.String -> {
                        formatAsString(property.key, property.value)
                    }
                }
                sb.append(line)
            }
        return sb.toString()
    }

    private fun formatAsString(key: String, value: String): String {
        val formattedKey = dedotkey(key)
        val type = KotlinType.String.name
        return "val $formattedKey: $type by project.extra { \"$value\" }\r\n"
    }

    private fun formatAsInt(key: String, value: String): String {
        val formattedKey = dedotkey(key)
        val type = KotlinType.Int.name
        return "val $formattedKey: $type by project.extra { $value }\r\n"
    }

}

internal class KlutterGradleFileWriter(val path: Path, val content: String): KlutterWriter {

    override fun write(): KlutterLogger {
        val logger = KlutterLogger()
        val file = path.resolve("klutter.gradle.kts").toAbsolutePath().toFile()

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