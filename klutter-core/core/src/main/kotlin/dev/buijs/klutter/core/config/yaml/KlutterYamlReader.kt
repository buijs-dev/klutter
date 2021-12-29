package dev.buijs.klutter.core.config.yaml

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.log.KlutterLogger
import java.io.File

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class KlutterYamlReader {

    private val logger = KlutterLogger()

    fun read(yaml: File): List<KlutterYamlProperty> {
        if (!yaml.exists()) throw KlutterConfigException("missing klutter.yaml file")

        val properties = mutableListOf<KlutterYamlProperty>()

        yaml.forEachLine { line ->
            var step: YamlProcesStep = YamlProcesStepStart(line)

            step.setLogger(logger)

            while(step.hasNext()) {
               step = step.execute()!!
            }

            if(step is YamlProcesStepSuccess) {
                step.property?.let {
                    properties.add(it)
                }
            }
        }

       return properties
    }
}