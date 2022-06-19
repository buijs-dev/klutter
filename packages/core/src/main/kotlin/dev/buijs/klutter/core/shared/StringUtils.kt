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

package dev.buijs.klutter.core.shared

/**
 * Return the current String value post-fixed with '.toKJson()'
 * if Method dataType is not a standard Dart/Kotlin type.
 */
internal fun String.maybePostfixToKJson() =
    DartKotlinMap.toMapOrNull(this)?.let { "" } ?: ".toKJson()"

/**
 * Return the current String value post-fixed with the given value.
 */
internal fun String.postFix(value: String) = "$this$value"

/**
 * Convert a String to camelCase.
 */
internal fun String.toCamelCase(): String {

    var hasUnderscore = false

    return lowercase().map {
        when {

            it == '_' -> {
                hasUnderscore = true
                ""
            }

            hasUnderscore -> {
                hasUnderscore = false
                it.uppercase()
            }

            else -> it.toString()
        }
    }.joinToString("") { it }

}

/**
 * Try to get the data type nested within a 'List<...>'.
 *
 * @return nested datatype if found or value of this String if not.
 */
internal fun String.unwrapFromList(): String {

    // Check if current String value is a List.
    val match = """List<([^>]+?)>""".toRegex().find(this)

        // Return current value if no match found.
        ?: return this

    // Return the extract value from within the List type.
    return match.groupValues[1]

}

/**
 * Extract the class name from a (sub) String which contains a single class definition.
 */
internal fun String.findClassName(): String? {

    // Remove linebreaks to make regex matching easier.
    val it = replace("\n", "")

    // Check if current String value contains 'class {'.
    val match = """class([^{]+?)\{""".toRegex().find(it)

        // Return null if no match found
        ?: return null

    // Return the extract class name.
    return match.groupValues[1].trim()

}

/**
 * @param [group] Int value group to return.
 * @return String value from MatchResult without any whitespaces.
 */
internal fun MatchResult.groupValueWithoutSpaces(
    group: Int,
): String = groupValues[group].filter { !it.isWhitespace() }