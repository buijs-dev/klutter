package dev.buijs.klutter.core.project

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.verifyExists
import java.io.File

/**
 * Wrapper class with a file instance pointing to the android sub-module.
 *
 * @property folder path to the Android folder.
 */
class Android(
    val folder: File,
    val pluginPackageName: String,
    val pluginClassName: String,
) {

    /**
     * Return path to android/src/main/AndroidManifest.xml.
     *
     * @throws KlutterException if path/file does not exist.
     * @return [File] AndroidManifest.xml.
     */
    val manifest = folder.verifyExists()
        .resolve("src/main")
        .verifyExists()
        .resolve("AndroidManifest.xml")
        .verifyExists()

    val pathToPlugin: File = folder
        .resolve("src/main/kotlin/${pluginPackageName.toPath()}")
        .verifyExists()
        .resolve("$pluginClassName.kt")

    private fun String?.toPath(): String =
        this?.replace(".", "/") ?: ""
}