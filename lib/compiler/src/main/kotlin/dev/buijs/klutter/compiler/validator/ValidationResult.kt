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
package dev.buijs.klutter.compiler.validator

import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk

/**
 * Alias for [Either] with only right value of type List of CustomType.
 */
internal typealias Valid =
        EitherOk<List<String>, List<Controller>>

/**
 * Alias for [Either] with only left value of type List of [String] (error).
 */
internal typealias Invalid =
        EitherNok<List<String>, List<Controller>>

/**
 * Response for [validateResponses].
 */
internal typealias ValidationResult =
        Either<List<String>, List<Controller>>

/**
 * Response for [validateResponses].
 */
internal typealias ValidationResultSquintMessages =
        Either<List<String>, List<SquintMessageSource>>

/**
 * Alias for [Either] with only right value of type List of CustomType.
 */
internal typealias ValidSquintMessages =
        EitherOk<List<String>, List<SquintMessageSource>>

/**
 * Alias for [Either] with only left value of type List of [String] (error).
 */
internal typealias InvalidSquintMessages =
        EitherNok<List<String>, List<SquintMessageSource>>

/**
 * Error indicating multiple Response classes have the same class name.
 */
internal fun duplicateResponseErrorSquint(types: List<String>) = InvalidSquintMessages(
    listOf("Response contract violation! Duplicate class names: $types")
)

/**
 * Error indicating a Controller has an unknown request and/or response Type.
 */
internal fun unknownResponseOrRequestError(types: List<String>) = Invalid(
    listOf("Unknown Response and/or Request Type: $types")
)

/**
 * Error indicating a Response class has a TypeMember of Unknown CustomType.
 */
internal fun unknownResponseErrorSquint(types: List<String>) = InvalidSquintMessages(
    listOf("Unknown Response TypeMember: $types")
)

/**
 * Error indicating the source File is missing.
 *
 * Without a source File, the dart squint library can not generate dart code.
 */
internal fun missingSourceFileErrorSquint(types: List<String>) = InvalidSquintMessages(
    listOf("Source File from which to generate dart code is missing: $types")
)

/**
 * Error indicating a Response class has no members.
 */
internal fun emptyResponseError(types: List<String>) = InvalidSquintMessages(
    listOf("Response contract violation! Some classes have no fields: $types")
)