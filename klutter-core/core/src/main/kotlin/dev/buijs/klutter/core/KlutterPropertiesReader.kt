package dev.buijs.klutter.core

import java.io.File

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class KlutterPropertiesReader(val file: File) {

    fun read(): HashMap<String, String> {
        if(!file.exists()) { throw KlutterConfigException("File not found: $file") }

        val properties = HashMap<String, String>()

        file.forEachLine {
            val pair = it.split("=")
            if(pair.size == 2){ properties[pair[0]] = pair[1] }
        }

        return properties
    }

}