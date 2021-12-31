package dev.buijs.klutter.core.config

import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterProducer
import dev.buijs.klutter.core.KotlinType
import java.nio.file.Path

class KlutterGradleProducer(
    private val path: Path,
    private val properties: List<YamlProperty>,
    private val logger: KlutterLogger
): KlutterProducer {

    override fun produce() {
        val file = path.resolve("klutter.gradle.kts").toAbsolutePath().toFile()

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
            val type = property.type
            val line = when(type) {
                YamlPropertyType.Int -> formatAsInt(key, value)
                YamlPropertyType.String -> formatAsString(key, value)
            }
            writer.append(line)
            logger.debug("Appending property '$key=$value' of type '$type' to file: $file")
        }
        writer.close()
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

internal fun dedotkey(dottie: String): String {
    var output = ""
    dottie.split(".").forEachIndexed { index, word ->
        output += if(index != 0) {
            word.replaceFirstChar { char -> char.uppercase() }
        } else word
    }
    return output
}