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
package dev.buijs.klutter.kore.common

fun String.prefixIfNot(prefix: String) =
    if(startsWith(prefix)) this else "$prefix$this"

fun String.removeSuffixIfPresent(suffix: String) =
    if(endsWith(suffix)) this.substringBeforeLast(suffix) else this

/**
 * Convert a String to camelCase.
 */
fun String.toCamelCase(): String {

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

private val isAlphabeticRegex = """^[a-zA-Z]$""".toRegex()

/**
 * Convert a String to snake_case.
 */
fun String.toSnakeCase(): String = mapIndexed { index, char ->
    when {
        index == 0 -> char.lowercase()

        !isAlphabeticRegex.matches("$char") -> char

        char.uppercase() == "$char" -> "_${char.lowercase()}"

        else -> char
    }
}.map { "$it" }.joinToString("") { it }