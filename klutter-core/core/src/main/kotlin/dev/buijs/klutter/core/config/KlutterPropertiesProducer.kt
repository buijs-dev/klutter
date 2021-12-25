package dev.buijs.klutter.core.config

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.log.KlutterLogging
import java.nio.file.Path
import kotlin.io.path.exists


/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 */
class KlutterPropertiesProducer {

    private val logger = KlutterLogging()

    fun produce(module: Path, properties: HashMap<String, String>): KlutterLogging {
        val directory = createKlutterDirectory(module)
        writeConfigGradleFile(directory, properties)
        return logger
    }

    private fun writeConfigGradleFile(path: Path, properties: HashMap<String, String>) {
        val file = path.resolve("config.gradle.kts").toAbsolutePath().toFile()

        if(file.exists()){
            logger.info("Deleting existing file: $file")
            file.delete()
        }

        file.createNewFile()
        logger.info("Created new file: $file")

        val writer = file.bufferedWriter()
        properties.forEach { (k, v) ->
            writer.append("project.extra[\"$k\"] = \"$v\"\r\n")
            logger.info("Appending property '$k=$v' to file: $file")
        }
        writer.close()
    }

    private fun createKlutterDirectory(root: Path): Path {
        if(!root.exists()){
            throw KlutterConfigException("Path to module directory does not exist: $root")
        }

        val directory = root.resolve(".klutter").toAbsolutePath().toFile()
        logger.info("Created directory: $directory")
        directory.mkdirs()
        return directory.toPath()
    }

}