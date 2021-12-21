package dev.buijs.klutter.gradle

import dev.buijs.klutter.core.config.KlutterPropertiesProducer
import dev.buijs.klutter.core.config.KlutterYamlReader
import dev.buijs.klutter.core.log.KlutterLogging
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutputFactory
import javax.inject.Inject

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
open class KlutterProduceConfigTask @Inject constructor(
    private val styledTextOutputFactory: StyledTextOutputFactory
): DefaultTask() {

    init {
        group = "produceConfig"
    }

    @TaskAction
    fun produceConfig() {
        val ext = project.klutteradapter()
        val modules = ext.modules
        val logger = KlutterLogging()

        val yaml = project.rootDir.toPath().resolve("klutter.yaml").toAbsolutePath().toFile()
        val properties = KlutterYamlReader().read(yaml)
        modules.forEach { module ->
            if(module.exists()) {
                val logging = KlutterPropertiesProducer().produce(module.toPath(), properties)
                logger.messages().addAll(logging.messages())
            } else logger.error("Module directory does not exist: ${module.absolutePath}")
        }

        val output = styledTextOutputFactory.create(javaClass.name)
        GradleLoggingWrapper(logger, output).sout()
    }

}