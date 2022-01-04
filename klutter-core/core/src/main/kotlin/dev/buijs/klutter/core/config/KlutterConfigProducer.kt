package dev.buijs.klutter.core.config

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.KlutterProducer
import dev.buijs.klutter.core.KlutterLogger
import java.nio.file.Path
import kotlin.io.path.exists


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
@Suppress("unused")
class KlutterConfigProducer(
    private val module: Path,
    private val properties: List<YamlProperty>): KlutterProducer {

    val logger = KlutterLogger()

    override fun produce(): KlutterLogger {
        val directory = createKlutterDirectory(module)
        val gradleGenerator = KlutterGradleFileGenerator(directory, properties)
        val propertiesGenerator = KlutterPropertiesGenerator(directory, properties)
        return logger
            .merge(gradleGenerator.generate())
            .merge(propertiesGenerator.generate())
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