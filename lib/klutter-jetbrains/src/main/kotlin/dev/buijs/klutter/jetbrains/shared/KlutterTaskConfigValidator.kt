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
package dev.buijs.klutter.jetbrains.shared

/**
 * Validate a [KlutterTaskConfig] instance.
 */
fun KlutterTaskConfig.validate(): ValidationResult {
    val messages = mutableListOf<String>()

    val appName = appName ?: klutterPluginDefaultName
    if(!appName.isValidAppName()) {
        messages.add("Invalid app name")
    }

    val groupName = groupName ?: klutterPluginDefaultGroup
    if(!groupName.isValidGroupName()) {
        messages.add("Invalid group name")
    }

    return ValidationResult(messages)
}

/**
 * Validate a Klutter app name with the following constraints:
 * - All characters are lowercase
 * - All characters are alphabetic or
 * - numeric or
 * - '_'
 * - Should start with an alphabetic character
 */
fun String.isValidAppName(): Boolean =
    """^[a-z][a-z0-9_]+$""".toRegex().matches(this)

/**
 * Validate a Klutter group name with the following constraints:
 * - All characters are lowercase
 * - All characters are:
 * - alphabetic or
 * - numeric or
 * - '_' or
 * - '.'
 *
 * - Group contains at least 2 parts (e.g. there is minimally 1 dot)
 * - Should start with an alphabetic character
 */
fun String.isValidGroupName(): Boolean {

    // Group should contain at least 2 group parts.
    if(!this.contains(".")) return false

    // Characters . and _ can not precede each other.
    if(this.contains("_.")) return false

    // Characters . and _ can not precede each other.
    if(this.contains("._")) return false

    // Name should be lowercase alphabetic separated by dots.
    return """^[a-z][a-z0-9._]+[a-z]$""".toRegex().matches(this)

}

/**
 * Result returned after validating user input.
 */
data class ValidationResult(
    val messages: List<String>,
) {
    val isValid: Boolean = messages.isEmpty()
}