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

import dev.buijs.klutter.core.KlutterException

/**
 * Enumeration value defined in Dart language.
 *
 * @property name enum class name.
 * @property values the enum constants.
 * @property valuesJSON the serializable values.
 */
internal data class DartEnum(
    val name: String,
    val values: List<String>,
    val valuesJSON: List<String>,
)

/**
 * Convert String content of File to a DartEnum object.
 *
 * @return [DartEnum]
 */
internal fun String.toDartEnum(name: String) = this
    .ifBlank { throw KlutterException("Unable to process enumeration: Enum has no values") }
    .splitValues()
    .toEnum(name)

private fun Map<String, String>.toEnum(
    name: String
) = DartEnum(
    name = name,
    values = keys.toList(),
    valuesJSON = values.toList(),
)

private fun String.splitValues(): Map<String, String> = toValues().let { s ->
    val values = if(s.hasAnnotations()) s
        .map { it.substringAfterLast(")") }
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    else s

    val valuesJSON = if(s.hasAnnotations()) s
        .map { it.substringBetween('"', '"') }
        .filter { it.isNotEmpty() }
    else s

    values.zip(valuesJSON).toMap()
}

private fun String.toValues() = this
    .substringBetween('{', '}')
    .split(",")
    .map { it.trim() }

private fun List<String>.hasAnnotations() =
    any { it.contains("@") }

private fun String.substringBetween(
    from: Char,
    to: Char,
) = substringAfter(from).substringBefore(to).trim()