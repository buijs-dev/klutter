package dev.buijs.klutter.core.config

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.log.KlutterLogging
import java.io.File

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 */
class KlutterYamlReader {

    private val logger = KlutterLogging()

    fun read(yaml: File): HashMap<String,String> {
        if (!yaml.exists()) throw KlutterConfigException("missing klutter.yaml file")

        val properties = HashMap<String, String>()

        //Space count to check indenting level
        val sub1 = 2
        val sub2 = 6
        val sub3 = 8

        //list of key sub parts
        var keyParts: MutableList<String> = mutableListOf()

        yaml.forEachLine { line ->
            if (line == "") {
                logger.debug(yamlMessage("Ignoring empty line"))
            } else {
                logger.debug(yamlMessage("Current (sub) key is ==> '${keyParts.joinToString(".")}'"))
                logger.debug(yamlMessage("Processing YAML line ==> '$line'"))
                //every nested line contains a '-' char so
                //if it's missing than a new block starts.
                if (!line.contains("-")) {
                    logger.debug(yamlMessage(("Current line in YAML starts a new property block.")))
                    keyParts = mutableListOf()
                    keyParts.add(line.filter { !it.isWhitespace() }.removeSuffix(":"))
                    logger.debug(yamlMessage("Cleared the (sub) keys and added new key ==> '${keyParts.joinToString(".")}'"))
                } else {
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
                    if (splitted[1] == "") {
                        logger.debug(yamlMessage("Current line in YAML has indenting and no value."))
                        logger.debug(yamlMessage("Procesing line as a sub header ==> '$line'"))

                        when (index) {
                            sub1 -> {
                                logger.debug(yamlMessage("Current line is sub header 1."))
                                val main = keyParts[0]
                                keyParts = mutableListOf(main)
                            }
                            sub2 -> {
                                logger.debug(yamlMessage("Current line is sub header 2."))
                                val main = keyParts[0]
                                val subh1 = keyParts[1]
                                keyParts = mutableListOf(main, subh1)
                            }
                            sub3 -> {
                                logger.debug(yamlMessage("Current line is sub header 3."))
                                val main = keyParts[0]
                                val subh1 = keyParts[1]
                                val subh2 = keyParts[2]
                                keyParts = mutableListOf(main, subh1, subh2)
                            }
                            else -> throw KlutterConfigException("YAML has greater indenting than can be read.")
                        }

                        val sub = splitted[0]
                            .filter { !it.isWhitespace() }
                            .removePrefix("-")
                            .removeSuffix(":")
                        logger.debug(yamlMessage("Extracted sub header part of line ==> $sub"))
                        keyParts.add(sub)
                        logger.debug(yamlMessage("Current (sub) key is ==> '${keyParts.joinToString(".")}'"))
                    } else {
                        logger.debug(yamlMessage("Current line has key and value ==> $line"))
                        val keyPrefix = when (index) {
                            sub1 -> keyParts[0]
                            sub2 -> "${keyParts[0]}.${keyParts[1]}"
                            sub3 -> "${keyParts[0]}.${keyParts[1]}.${keyParts[2]}"
                            else -> throw KlutterConfigException("YAML has greater indenting than can be read.")
                        }

                        val value = if (line.contains('"')) {
                            line.substringAfter('"').substringBeforeLast('"')
                        } else splitted[1].filter { !it.isWhitespace() }

                        val name = "$keyPrefix.${
                            splitted[0]
                                .filter { !it.isWhitespace() }
                                .removePrefix("-")
                                .removeSuffix(":")
                        }"

                        properties[name] = value
                        logger.info(yamlMessage("Adding property with key '$name' and value '$value'"))
                    }
                }
            }

        }

       return properties
    }
}