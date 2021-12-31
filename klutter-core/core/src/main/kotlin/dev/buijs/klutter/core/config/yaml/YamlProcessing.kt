package dev.buijs.klutter.core.config.yaml

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.config.yamlMessage
import dev.buijs.klutter.core.log.KlutterLogger

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
 */
private var log: KlutterLogger? = null
private val keyPartsProcessor: KeyPartsProcessor = KeyPartsProcessor()

/**
 * Superclass to control processing of YAML file.
 */
abstract class YamlProcesStep {

    abstract fun execute(): YamlProcesStep?

    abstract fun hasNext(): Boolean

    open fun property(): YamlProperty? = null

    fun getLogger(): KlutterLogger {

        if(log == null){
            log = KlutterLogger()
        }

        return log as KlutterLogger
    }

    fun setLogger(logger: KlutterLogger){ log = logger }

}

class YamlProcesStepStart(private val line: String): YamlProcesStep() {

    override fun hasNext() = true

    override fun execute(): YamlProcesStep {
        if(line == "") return YamlProcesEmptyLine()

        getLogger().debug(yamlMessage("Current (sub) key is ==> '${keyPartsProcessor.toKey()}'"))
        getLogger().debug(yamlMessage("Processing YAML line ==> '$line'"))

        //every nested line contains a '-' char so
        //if it's missing than a new block starts.
        return if (!line.contains("-")) {
            YamlProcesNewBlock(line)
        } else YamlProcesSplitLine(line)
    }

}

class YamlProcesEmptyLine: YamlProcesStep() {

    override fun hasNext() = true

    override fun execute(): YamlProcesStep {
        getLogger().debug(yamlMessage("Ignoring empty line"))
        return YamlProcesStepSuccess()
    }
}

class YamlProcesSplitLine(private val line: String): YamlProcesStep() {

    override fun hasNext() = true

    override fun execute(): YamlProcesStep {

        if(!line.contains(":")) {
            return YamlProcesStepFailed("Line in yaml file is invalid because it does not contain ':' ")
        }

        val splitted = line.split(":")
        val chars = line.toCharArray()

        var index = 0
        var keepCounting = true
        while (keepCounting) {
            if (chars[index].isWhitespace()) {
                index += 1
            } else keepCounting = false
        }

        //if no value after ':' then it's a sub-header
        if (splitted[1] == "") return YamlProcesAsSubHeader(splitted, index)

        return YamlProcesCreateProperty(line, splitted)
    }
}

class YamlProcesAsSubHeader(
    private val splitted: List<String>,
    private val index: Int,
    ): YamlProcesStep() {

    override fun hasNext() = true

    override fun execute(): YamlProcesStepSuccess {
        getLogger().debug(yamlMessage("Current line in YAML has indenting and no value."))
        getLogger().debug(yamlMessage("Procesing line as a sub header ==> '${splitted.joinToString { ":" }}'"))

        keyPartsProcessor.clearAfterIndex(index)

        val sub = splitted[0]
            .filter { !it.isWhitespace() }
            .removePrefix("-")
            .removeSuffix(":")

        getLogger().debug(yamlMessage("Extracted sub header part of line ==> $sub"))
        keyPartsProcessor.add(sub)
        getLogger().debug(yamlMessage("Current (sub) key is ==> '${keyPartsProcessor.toKey()}'"))
        return YamlProcesStepSuccess()
    }
}

class YamlProcesCreateProperty(
    private val line: String,
    private val splitted: List<String>,
    ): YamlProcesStep() {

    override fun hasNext() = true

    override fun execute(): YamlProcesStep {

        getLogger().debug(yamlMessage("Current line has key and value ==> $line"))

        //count spaces from start of line until char '-' and divide by 2 to get the level of indenting
        val indenting = line.substringBefore("-").toCharArray().size.div(2)
        val keyPrefix = keyPartsProcessor.toSubKey(indenting)
        val type: YamlPropertyType

        val value = if (line.contains('"')) {
            type = YamlPropertyType.String
            line.substringAfter('"').substringBeforeLast('"')
        } else {
            type = YamlPropertyType.Int
            splitted[1].filter { !it.isWhitespace() }
        }

        val name = "$keyPrefix.${
            splitted[0]
                .filter { !it.isWhitespace() }
                .removePrefix("-")
                .removeSuffix(":")
        }"

        val property = YamlProperty(
            key = name,
            value = value,
            type = type
        )

        getLogger().info(yamlMessage("Adding property with key '$name' and value '$value' as type '$type'"))

        return YamlProcesStepSuccess(property)
    }

}

class YamlProcesNewBlock(private val line: String): YamlProcesStep() {

    override fun hasNext(): Boolean = true

    override fun execute(): YamlProcesStep {
        getLogger().debug(yamlMessage(("Current line in YAML starts a new property block.")))
        keyPartsProcessor.clear()
        keyPartsProcessor.add(line.filter { !it.isWhitespace() }.removeSuffix(":"))
        getLogger().debug(yamlMessage("Cleared the (sub) keys and added new key ==> '${keyPartsProcessor.toKey()}'"))
        return YamlProcesStepSuccess()
    }
}

class YamlProcesStepSuccess(val property: YamlProperty? = null): YamlProcesStep() {
    override fun hasNext() = false

    override fun execute(): YamlProcesStep? {
        getLogger().debug(yamlMessage("Line processing finished successfully"))
        return null
    }

    override fun property() = property
}

class YamlProcesStepFailed(private val message: String): YamlProcesStep() {
    override fun hasNext() = true

    override fun execute() = throw KlutterConfigException(message)
}

internal class KeyPartsProcessor {
    private var keyParts: MutableList<String> = mutableListOf()

    fun add(part: String) = keyParts.add(part)

    fun toKey(): String = keyParts.joinToString(".")

    fun toSubKey(indenting: Int): String {
        return if(keyParts.isNotEmpty()){
            var keyPrefix = keyParts[0]
            keyParts.forEachIndexed { index, s -> if(index in 1 until indenting) { keyPrefix += ".$s" } }
            keyPrefix
        } else ""
    }

    fun clear() = keyParts.clear()

    fun clearAfterIndex(indenting: Int){
        val temp = mutableListOf<String>()
        val until = indenting - 1
        keyParts.forEachIndexed { index, s -> if(index < until) temp.add(s) }
        keyParts.clear()
        keyParts.addAll(temp)
    }

}