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
package dev.buijs.klutter.kore.ast

import dev.buijs.klutter.kore.common.Either

/**
 * Regex to find a constructor field member.
 *
 * Example:
 * ```
 * val foo: Foo
 * ```
 */
private val constructorFieldRegex =
    """^(val|var)\s+?([a-zA-Z_][a-zA-Z_0-9]*?):\s+?([A-Za-z][a-zA-Z_0-9<>,]*+[?]*$)""".toRegex()

/**
 * Error indicating no match was returned when matching [constructorFieldRegex].
 */
private fun String.constructorFieldRegexError() =
    InvalidTypeMember("Field member could not be processed: '$this'")

/**
 * Error indicating a field member is not immutable.
 */
private fun String.mutabilityError() =
    InvalidTypeMember("Field member is mutable: '$this'")

/**
 * Process a constructor member declaration and return it as [TypeMember].
 *
 * Expected input:
 *
 * ```
 * val foo: Foo
 * ```
 */
fun String.toTypeMember(): Either<String, TypeMember> {

    val match = constructorFieldOrNull()
        ?: return constructorFieldRegexError()

    val values = match.immutableFieldOrNull()
        ?: return mutabilityError()

    val name = values[2].trim()

    val type = TypeData(values[3]).toAbstractType()

    return if(type is ValidAbstractType) {
        ValidTypeMember(data = TypeMember(name, type.data))
    } else {
        InvalidTypeMember(data = (type as InvalidAbstractType).data)
    }

}

private fun String.constructorFieldOrNull() =
    constructorFieldRegex.find(this)

private fun MatchResult.immutableFieldOrNull() =
    if(groupValues[1].trim() == "val") groupValues else null
