/* Copyright (c) 2021 - 2023 Buijs Software
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
package dev.buijs.klutter.compiler.scanner

import dev.buijs.klutter.compiler.wrapper.KCMessage
import dev.buijs.klutter.compiler.wrapper.KCResponse
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk

/**
 * Alias for [Either] with only right value of type [SquintMessageSource].
 */
typealias ValidSquintType =
        EitherOk<String, SquintMessageSource>

/**
 * Alias for [Either] with only left value of type [String] (error).
 */
typealias InvalidSquintType =
        EitherNok<String, SquintMessageSource>

/**
 * Error indicating a class is missing the @Response annotation.
 */
internal fun KCResponse.missingSerializableAnnotation() =
    InvalidSquintType("Class is missing @Serializable annotation: $packageName.$className")

/**
 * Error indicating a class does not extend JSON.
 */
internal fun KCMessage.doesNotExtendKlutterJSON() =
    InvalidSquintType("Class does not extend JSON: $packageName.$className")

/**
 * A Response is invalid because it has multiple constructors.
 */
internal fun KCResponse.responseHasTooManyConstructors() =
    InvalidSquintType("Response $packageName.$className has multiple constructors but only 1 is allowed.")

/**
 * A Response is invalid because it has no constructor fields.
 */
internal fun KCResponse.emptyConstructor() =
    InvalidSquintType("Response $packageName.$className constructor has no fields but at least 1 is expected.")

/**
 * Error indicating a field member is not immutable.
 */
internal fun String.mutabilityError() =
    InvalidTypeMember("TypeMember is mutable: '$this'")

internal fun KCMessage.invalidTypeMembers(invalidTypeMembers: List<String>) =
    InvalidSquintType("Response $packageName.$className is invalid: ${invalidTypeMembers.joinToString { it }}")