package dev.buijs.klutter.core.config.yaml

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.log.KlutterLogger
import java.io.File

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class YamlReader {

    private val logger = KlutterLogger()

    fun read(yaml: File): List<YamlProperty> {
        if (!yaml.exists()) throw KlutterConfigException("File does not exist: $yaml")

        val properties = mutableListOf<YamlProperty>()

        yaml.forEachLine { line ->
            var step: YamlProcesStep = YamlProcesStepStart(line).also { it.setLogger(logger) }

            while(step.hasNext()) { step = step.execute()!! }

            if(step is YamlProcesStepSuccess) { step.property?.let { properties.add(it) } }
        }

       return properties
    }
}