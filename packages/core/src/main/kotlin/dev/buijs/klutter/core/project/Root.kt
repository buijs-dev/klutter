package dev.buijs.klutter.core.project

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.toCamelCase
import java.io.File

/**
 * @property folder path to the top level of the project.
 */
class Root(val pluginName: String, file: File) {

    val folder: File =
        if (!file.exists()) {
            throw KlutterException("The root folder does not exist: ${file.absolutePath}.")
        } else {
            file.absoluteFile
        }

    val pathToLib = resolve("lib/$pluginName.dart")

    val pluginClassName = pluginName
        .toCamelCase()
        .replaceFirstChar { it.uppercase() }

    fun resolve(to: String): File = folder.resolve(to).normalize().absoluteFile
}