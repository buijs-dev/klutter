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
package dev.buijs.klutter.compiler.scanner

import dev.buijs.klutter.compiler.processor.kcLogger
import dev.buijs.klutter.compiler.validator.isValidRequestParameterType
import dev.buijs.klutter.compiler.wrapper.KAWrapper
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.Either

/**
 * Find all methods annotated with @Event.
 */
internal fun Sequence<KAWrapper>.getEvents(): List<Either<String, Method>> = this
    .filter { it.hasEventAnnotation }
    .map { it.toMethodOrError() }
    .toList()

internal fun String.determineAbstractTypeOrFail(
    responses: Set<AbstractType>
): Either<String,AbstractType> {

    kcLogger?.info("Execute [determineAbstractTypeOrFail] for value '$this' with known responses: $responses")

    val typeOrError = TypeData(this).toStandardTypeOrUndetermined()

    if(typeOrError !is ValidAbstractType)
        return typeOrError.also { kcLogger?.info("Encountered invalid Type during [determineAbstractTypeOrFail]: $it") }

    val data = typeOrError.data

    if (data is StandardType)
        return Either.ok(data.also { kcLogger?.info("Encountered StandardType during [determineAbstractTypeOrFail]: $it") })

    val response = responses
        .firstOrNull { it.className == data.className }

    if(response != null)
        return Either.ok(response.also { kcLogger?.info("Encountered CustomType during [determineAbstractTypeOrFail]: $it") })

    return typeOrError.also { kcLogger?.warn("Encountered unknown Type during [determineAbstractTypeOrFail]: $it") }
}

private fun KAWrapper.toMethodOrError(): Either<String, Method>  = when {
    errorMessageOrNull != null ->
        InvalidEvent(errorMessageOrNull)
    method == null ->
        InvalidEvent(eventMethodConversionFailure)
    method.requestDataType == null ->
        ValidEvent(method)
    !method.requestDataType!!.isValidRequestParameterType() ->
        eventUnsupportedRequestParameterType()
    else -> ValidEvent(method)
}