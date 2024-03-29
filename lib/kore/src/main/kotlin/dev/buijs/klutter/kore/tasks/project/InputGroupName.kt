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
package dev.buijs.klutter.kore.tasks.project

import dev.buijs.klutter.kore.common.Either

typealias GroupName = Either<String, String>

fun toGroupName(value: String) = value.toInput()

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
private fun String.toInput(): GroupName {

    if(!contains("."))
        return GroupName.nok("GroupName error: Should contain at least 2 parts ('com.example').")

    if(contains("_."))
        return GroupName.nok("GroupName error: Characters . and _ can not precede each other.")

    if(contains("._"))
        return GroupName.nok("GroupName error: Characters . and _ can not precede each other.")

    if(!"""^[a-z][a-z0-9._]+[a-z]$""".toRegex().matches(this))
        return GroupName.nok("GroupName error: Should be lowercase alphabetic separated by dots ('com.example').")

    return GroupName.ok(this)
}