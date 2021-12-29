package dev.buijs.klutter.core.utils

import dev.buijs.klutter.core.KlutterConfigException
import java.io.File

internal fun readPropertiesFromFile(file: File): HashMap<String, String> {
    if(!file.exists()) { throw KlutterConfigException("File not found: $file") }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){ properties[pair[0]] = pair[1] }
    }

    return properties
}