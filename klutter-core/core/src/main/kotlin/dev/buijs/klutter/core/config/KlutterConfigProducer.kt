package dev.buijs.klutter.core.config

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.adapter.service.KotlinType
import dev.buijs.klutter.core.config.yaml.KlutterYamlProperty
import dev.buijs.klutter.core.config.yaml.KlutterYamlPropertyType
import dev.buijs.klutter.core.log.KlutterLogger
import java.nio.file.Path
import kotlin.io.path.exists


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class KlutterConfigProducer {

    private val logger = KlutterLogger()

    fun produce(module: Path, properties: List<KlutterYamlProperty>): KlutterLogger {
        val directory = createKlutterDirectory(module)
        writeConfigGradleFile(directory, properties)
        return logger
    }

    private fun writeConfigGradleFile(path: Path, properties: List<KlutterYamlProperty>) {
        val file = path.resolve("config.gradle.kts").toAbsolutePath().toFile()

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
                KlutterYamlPropertyType.Int -> formatAsInt(key, value)
                KlutterYamlPropertyType.String -> formatAsString(key, value)
            }
            writer.append(line)
            logger.debug("Appending property '$key=$value' of type '$type' to file: $file")
        }
        writer.close()
    }

    private fun createKlutterDirectory(root: Path): Path {
        if(!root.exists()){
            throw KlutterConfigException("Path to module directory does not exist: $root")
        }

        val directory = root.resolve(".klutter").toAbsolutePath().toFile()
        directory.mkdirs()
        logger.info("Created directory: $directory")
        return directory.toPath()
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

internal fun formatAsString(key: String, value: String): String {
    val formattedKey = dedotkey(key)
    val type = KotlinType.String.name
    return "val $formattedKey: $type by project.extra { \"$value\" }\r\n"
}

internal fun formatAsInt(key: String, value: String): String {
    val formattedKey = dedotkey(key)
    val type = KotlinType.Int.name
    return "val $formattedKey: $type by project.extra { $value }\r\n"
}