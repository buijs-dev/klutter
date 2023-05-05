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

import com.google.devtools.ksp.symbol.KSClassDeclaration
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.Either

/**
 * A Controller is invalid because it's constructor has fields.
 * </br>
 * A Controller should only have a no-args constructor.
 */
internal fun Any.controllerIsMissingNoArgsConstructor() = Either.nok<String, Controller>(
    "Controller $this is only allowed to have a no-arg constructor")

/**
 * A Controller is invalid because it has multiple constructors.
 *
 * A Controller should only have a no-args constructor.
 */
internal fun Any.controllerHasTooManyConstructors() = Either.nok<String, Controller>(
    "Controller $this has multiple constructors but only 1 is allowed.")

internal fun List<InvalidEvent>.controllerHasInvalidEvents() =
    InvalidController(joinToString { it.data })

/**
 * A Publisher Controller is invalid because it's TypeParameter is not a Response class.
 * </br>
 * A Response class should extend KlutterJSON and be annotated with "@Response".
 */
internal fun String.publisherControllerHasInvalidTypeParameter() =
    InvalidController("Publisher has invalid TypeParameter: $this (not a Response class)")

/**
 * Return a [ValidController] containing a [RequestScopedBroadcastController].
 */
internal fun KSClassDeclaration.validRequestScopedBroadcastController(functions: List<Method>, response: AbstractType) =
    ValidController(
        RequestScopedBroadcastController(
            packageName = packageName.asString(),
            className = "$this",
            functions = functions,
            response = response))

/**
 * Return a [ValidController] containing a [SingletonBroadcastController].
 */
internal fun KSClassDeclaration.validSingletonBroadcastController(functions: List<Method>, response: AbstractType) =
    ValidController(
        SingletonBroadcastController(
            packageName = packageName.asString(),
            className = "$this",
            functions = functions,
            response = response))

/**
 * Return a [ValidController] containing a [RequestScopedSimpleController].
 */
internal fun KSClassDeclaration.validRequestScopedSimpleController(functions: List<Method>) =
    ValidController(
        RequestScopedSimpleController(
            packageName = packageName.asString(),
            className = "$this",
            functions = functions))

/**
 * Return a [ValidController] containing a [SingletonSimpleController].
 */
internal fun KSClassDeclaration.validSingletonSimpleController(functions: List<Method>) =
    ValidController(
        SingletonSimpleController(
            packageName = packageName.asString(),
            className = "$this",
            functions = functions))