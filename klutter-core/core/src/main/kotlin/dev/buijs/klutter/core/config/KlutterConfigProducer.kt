package dev.buijs.klutter.core.config

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.config.yaml.YamlProperty
import dev.buijs.klutter.core.log.KlutterLogger
import java.nio.file.Path
import kotlin.io.path.exists


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class KlutterConfigProducer {

    private val logger = KlutterLogger()

    fun produce(module: Path, properties: List<YamlProperty>): KlutterLogger {
        val directory = createKlutterDirectory(module)
        KlutterGradleProducer(directory, properties, logger).produce()
        KlutterPropertiesProducer(directory, properties, logger).produce()
        return logger
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