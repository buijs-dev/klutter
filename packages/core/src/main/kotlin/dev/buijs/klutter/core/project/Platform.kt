package dev.buijs.klutter.core.project

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.shared.verifyExists
import java.io.File


/**
 * Wrapper class with a file instance pointing to the kmp sub-module in root/platform.
 *
 * @property folder path to the Platform folder.
 */
class Platform(
    val folder: File,
    private val pluginName: String,
) {

    /**
     * Function to return the location of the src module containing the common/shared platform code.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the common source code.
     */
    fun source() = folder
        .verifyExists()
        .resolve("src/commonMain")
        .verifyExists()

    /**
     * Function to return the location of the podspec in the platform sub-module.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun podspec() = folder
        .verifyExists()
        .resolve("$pluginName.podspec")
        .verifyExists()
}