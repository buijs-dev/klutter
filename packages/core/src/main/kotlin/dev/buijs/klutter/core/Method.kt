/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.buijs.klutter.core

import dev.buijs.klutter.core.annotations.ReturnTypeLanguage
import dev.buijs.klutter.core.project.Pubspec
import org.jetbrains.kotlin.util.removeSuffixIfPresent
import java.io.File

private typealias Lang = ReturnTypeLanguage

/**
 * Data class to contain information about analyzed Kotlin (Platform) code
 * which is used to generate Dart code in the Flutter lib folder.
 */
internal data class Method(

    /**
     * The command to execute a method-channel method.
     *
     * In the following example 'greeting' is the command:
     *
     * ```
     * static Future<AdapterResponse<String>> get greeting async {
     *  ...
     * }
     * ```
     */
    val command: String,

    /**
     * The full import statement as required in Android to call a platform method.
     */
    val import: String,

    /**
     * The actual platform method signature to be executed.
     *
     * In the following example 'Greeting().greeting()' is the method:
     *
     * ```
     * override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
     *    mainScope.launch {
     *      when (call.method) {
     *"         greeting" -> {
     *              result.success(Greeting().greeting())
     *          }
     *          else -> result.notImplemented()
     *      }
     *   }
     *}
     * ```
     */
    val method: String,

    /**
     * [Boolean] value indicating a method to be called is asynchronous or not.
     *
     * Asynchronous code requires more boilerplate code to be generated by Klutter.
     */
    val async: Boolean = false,

    /**
     * The type of data returned when calling the [method].
     *
     * Can be a standard Kotlin/Dart type as defined in [DartKotlinMap]
     * or a custom defined type.
     */
    val dataType: String)

private const val REGEX =
    """@KlutterAdaptee\(("|[^"]+?")([^"]+?)".+?(suspend|)fun([^(]+?\([^:]+?):([^{]+?)\{"""

/**
 * The method-channel name which uses the package name
 * defined in the pubspec.yaml or
 * defaults to <plugin-name>.klutter if not present.
 */
internal fun Pubspec.toChannelName(): String =
    android?.pluginPackage ?: "$name.klutter"

internal fun File.toMethods(
    language: Lang = Lang.KOTLIN,
): List<Method> {

    val content = readText()

    /**
     * Name of Kotlin file without extension
     * used to import the class
     * and use it's enclosing methods.
     */
    val className = name.removeSuffixIfPresent(".kt")

    /**
     * Name of package used to import the Kotlin class.
     */
    val packageName = content.packageName()

    return REGEX.toRegex().findAll(content.filter { !it.isWhitespace() })
        .map { it.groupValues }
        .map { values -> values.toMethod(className, packageName, language)}
        .toList()
}

/**
 * @return [Method] based of [REGEX] result.
 */
private fun List<String>.toMethod(
    className: String,
    packageName: String,
    language: Lang,
): Method {

    /**
     * The import statement required to use this method.
     *
     * Example:
     *
     * Class [className] Foo in package with name [packageName] com.example
     * should be import value 'com.example.Foo'.
     */
    val import = "$packageName.$className"

    /**
     * The command name used by the method-channel handler.
     *
     * Example:
     *
     * ```
     * @KlutterAdaptee("greetingInfo")
     * fun infoAboutGreeting(): String {
     *  ...
     * }
     * ```
     *
     * Command should be 'greetingInfo'.
     */
    val command = this[2]

    /**
     * The actual method call signature.
     *
     * Example:
     *
     * ```
     * Class Bar {
     *   @KlutterAdaptee("greetingInfo")
     *   fun infoAboutGreeting(): String {
     *       ...
     *    }
     * }
     * ```
     *
     * Method should be 'Bar().infoAboutGreeting()'.
     */
    val method = "$className().${this[4]}"

    /**
     * Boolean value indicating a method is asynchronous.
     */
    val async = this[3] == "suspend"

    /**
     * The data type of the return value.
     */
    val type = this[5].asDataType(language)

    return Method(
        import = import,
        command = command,
        method = method,
        async = async,
        dataType = type,
    )
}

/**
 * Extract the package name to be used as import statement in generated code.
 */
private fun String.packageName(): String {

    val result = """package(.*)""".toRegex().find(this)

    return if(result == null) "" else result.groupValues[1].trim()

}


/**
 * Converts the data type if possible.
 *
 * If the value is found in [DartKotlinMap]:
 * - for [language] DART returns the standard Dart type.
 * - for [language] KOTLIN returns the standard Kotlin type.
 *
 * If no value found then this value is returned as-is.
 */
private fun String.asDataType(language: Lang): String {

    /**
     * Remove any whitespaces if present.
     */
    val value = this.trim()

    /**
     * Lookup this [String] value in the DartKotlinMap.
     *
     * If no value is found in DartKotlinMap then return this.
     */
    val dartKotlinType = DartKotlinMap.toMapOrNull(value)

    return when {

        /**
         * Return current value as custom data type.
         */
        dartKotlinType == null -> {
            value
        }

        /**
         * Return standard DART data type.
         */
        language == Lang.DART -> {
            dartKotlinType.dartType
        }

        /**
         * Return standard KOTLIN data type.
         */
        else -> {
            dartKotlinType.kotlinType
        }
    }
}