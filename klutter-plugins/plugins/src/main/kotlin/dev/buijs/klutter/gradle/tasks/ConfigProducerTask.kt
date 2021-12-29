package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.config.KlutterConfigProducer
import dev.buijs.klutter.core.config.yaml.KlutterYamlProperty
import dev.buijs.klutter.core.config.yaml.KlutterYamlReader
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.io.File
import javax.inject.Inject
import kotlin.collections.HashMap

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
        val klutter = project.rootDir.toPath().resolve("klutter").toFile()
        val modules = mutableListOf(klutter)
        modules.addAll(modules())
        modules.forEach { module ->
            if(module.exists()) {
                val logging = KlutterConfigProducer().produce(module.toPath(), properties)
                logger.messages().addAll(logging.messages())
            } else logger.error("Module directory does not exist: ${module.absolutePath}")
        }
    }

    private fun getProperties(): List<KlutterYamlProperty> {
        val configYaml = getFile(filename = "klutter/klutter.yaml", failWhenNotExists = true)!!
        val secretYaml = getFile(filename = "klutter/klutter-secrets.yaml", failWhenNotExists = false)

        return if(secretYaml?.exists() == true){
            val config = KlutterYamlReader().read(configYaml)
            val secret = KlutterYamlReader().read(secretYaml)
            mutableListOf<KlutterYamlProperty>().also {
                it.addAll(config)
                it.addAll(secret)
            }
        } else KlutterYamlReader().read(configYaml)

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