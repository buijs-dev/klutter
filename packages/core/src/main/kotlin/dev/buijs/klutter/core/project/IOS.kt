@file:Suppress("MemberVisibilityCanBePrivate")
package dev.buijs.klutter.core.project

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.shared.verifyExists
import java.io.File

/**
 * Wrapper class with a path to the [root]/ios.
 *
 * @property folder path to the iOS folder.
 */
class IOS(
    val folder: File,
    val pluginName: String,
    val pluginClassName: String,
) {

    val pathToPlugin: File = folder.resolve("Classes/Swift$pluginClassName.swift")

    /**
     * Function to return the location of the PodFile in the ios sub-module.
     * If no custom path is given, Klutter assumes the path to the iOS Podfile is root-project/ios/PodFile.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun podfile() = folder
        .verifyExists()
        .resolve("Podfile")
        .verifyExists()

    /**
     * Function to return the location of the podspec in the ios sub-module.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun podspec() = folder
        .verifyExists()
        .resolve("$pluginName.podspec")
        .verifyExists()

    /**
     * Function to return the location of the AppDelegate.swift file in the ios folder.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the ios AppDelegate.
     */
    fun appDelegate() = folder
        .verifyExists()
        .resolve("Runner")
        .verifyExists()
        .resolve("AppDelegate.swift")
        .verifyExists()

}