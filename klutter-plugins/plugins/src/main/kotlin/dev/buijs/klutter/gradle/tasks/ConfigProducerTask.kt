package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.config.KlutterConfigProducer
import dev.buijs.klutter.core.config.YamlProperty
import dev.buijs.klutter.core.config.YamlReader
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.io.File
import javax.inject.Inject

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
open class ConfigProducerTask
@Inject constructor(styledTextOutputFactory: StyledTextOutputFactory):
    KlutterGradleTask(styledTextOutputFactory)
{
    /**
     * Write config to klutter module and every module defined in the Klutter Gradle Plugin
     */
    override fun describe() {
        val properties = getProperties()
        val modules = mutableListOf(project.rootDir)
        modules.addAll(modules()?: emptyList())
        modules.forEach { module ->
            if(module.exists()) {
                val producer = KlutterConfigProducer(module.toPath(), properties)
                producer.produce()
                logger.merge(producer.logger)
            } else logger.error("Module directory does not exist: ${module.absolutePath}")
        }
    }

    private fun getProperties(): List<YamlProperty> {
        val configYaml = getFile("klutter/klutter.yaml", failWhenNotExists = true)!!
        val localYaml  = getFile("klutter/klutter-local.yaml", failWhenNotExists = false)
        val secretYaml = getFile("klutter/klutter-secrets.yaml", failWhenNotExists = false)

        return mutableListOf<YamlProperty>().also {
            YamlReader().read(configYaml).also {
                properties -> it.addAll(properties)
            }

            secretYaml?.let { yaml -> YamlReader().read(yaml) }.also { maybeProperties ->
                maybeProperties?.let { properties -> it.addAll(properties) }
            }

            localYaml?.let { yaml -> YamlReader().read(yaml) }.also { maybeProperties ->
                maybeProperties?.let { properties -> it.addAll(properties) }
            }
        }

    }

    private fun getFile(filename: String, failWhenNotExists: Boolean = true): File? {
        val file = project.rootDir.resolve(filename).absoluteFile
        return if (!file.exists()) {
            if(failWhenNotExists) {
                throw KlutterConfigException("File location for '$name' does not exist: '${file.absolutePath}'")
            } else null
        } else file
    }

}