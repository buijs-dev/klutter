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

import dev.buijs.klutter.compiler.processor.kcLogger
import dev.buijs.klutter.compiler.scanner.InvalidController
import dev.buijs.klutter.compiler.scanner.ValidController
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.common.Either

/**
 * Find all valid Controller classes.
 * </br>
 * Will return either:
 * <ul>
 *     <li>List of [Controller] if all are valid.</li>
 *     <li>List of [String] errors if not.</li>
 * </ul>
 */
internal fun List<Either<String,Controller>>.validateControllers(responses: List<AbstractType>): ValidationResult {

    val errors = filterIsInstance<InvalidController>()

    if(errors.isNotEmpty())
        return Invalid(errors.map { it.data }.toList())

    val distinctTypes = this
        .filterIsInstance<ValidController>()
        .map { it.data }
        .distinctControllers()

    val unknownTypes =
        distinctTypes.unknownControllerRequestOrResponseType(responses)

    val unsupportedRequestParameter =
        distinctTypes.unsupportedRequestParameter()

    val unsupportedResponseParameter =
        distinctTypes.unsupportedResponseParameter()

    val unsupportedEventResponseType =
        distinctTypes.unsupportedEventResponseType()

    return when {
        unknownTypes.isNotEmpty() ->
            unknownResponseOrRequestError(unknownTypes)

        unsupportedRequestParameter.isNotEmpty() ->
            unsupportedRequestParameterError(unsupportedRequestParameter)

        unsupportedResponseParameter.isNotEmpty() ->
            unsupportedResponseParameterError(unsupportedResponseParameter)

        unsupportedEventResponseType.isNotEmpty() ->
            unsupportedResponseParameterError(unsupportedEventResponseType)

        else -> Valid(distinctTypes.toList())
    }
}

private fun List<Controller>.distinctControllers(): Set<Controller> {
    val set = mutableSetOf<CustomType>()
    forEach { set.addOrReplaceIfApplicable(it) }
    return set.filterIsInstance<Controller>().toSet()
}

private fun Set<Controller>.unknownControllerRequestOrResponseType(types: List<AbstractType>) = this
    .flatMap { it.functions }
    .flatMap { listOfNotNull(it.requestDataType, it.responseDataType) }
    .filter { it !is StandardType }
    .map { it.className }
    .filter { types.none { type -> type.className == it } }

private fun Set<Controller>.unsupportedRequestParameter() = this
    .flatMap { it.functions }
    .mapNotNull { it.requestDataType }
    .filter { !it.isValidRequestParameter() }
    .also {
        for (abstractType in it) {
            kcLogger?.warn("Found unsupported request Type: $it")
        }
    }
    .map { it.typeSimplename() }

private fun AbstractType.isValidRequestParameter(): Boolean = when(this) {
    is CustomType,
    is EnumType,
    is BooleanType,
    is DoubleType,
    is IntType,
    is LongType,
    is StringType -> true
    is ListType -> {
        when(child) {
            is BooleanType -> true
            is DoubleType -> true
            is IntType -> true
            is LongType -> true

            // Unsupported:
            is CustomType,
            is EnumType,
            is ByteArrayType,
            is DoubleArrayType,
            is FloatArrayType,
            is IntArrayType,
            is ListType,
            is LongArrayType,
            is MapType,
            is StringType,
            is UnitType,
            is UndeterminedType,
            null -> false
        }
    }

    // Unsupported:
    is MapType,
    is UnitType,
    is UndeterminedType,
    is ByteArrayType,
    is DoubleArrayType,
    is FloatArrayType,
    is IntArrayType,
    is LongArrayType -> false
}

private fun Set<Controller>.unsupportedEventResponseType() = this
    .flatMap { it.functions }
    .map { it.responseDataType }
    .filter { !it.isValidResponseParameter() }
    .filter { it !is UnitType } // void is allowed for Events
    .also {
        for (abstractType in it) {
            kcLogger?.warn("Found unsupported Event Response Type: $it")
        }
    }
    .map { it.typeSimplename() }

private fun Set<Controller>.unsupportedResponseParameter() = this
    .filterIsInstance<BroadcastController>()
    .filter { !it.response.isValidResponseParameter() }
    .also {
        for (abstractType in it) {
            kcLogger?.warn("Found unsupported Broadcast Response Type: $it")
        }
    }
    .map { it.typeSimplename() }

private fun AbstractType.isValidResponseParameter(): Boolean = when(this) {
    is CustomType,
    is EnumType,
    is BooleanType,
    is DoubleType,
    is IntType,
    is LongType,
    is StringType,
    is ByteArrayType,
    is DoubleArrayType,
    is FloatArrayType,
    is IntArrayType,
    is LongArrayType -> true
    is ListType -> {
        when(child) {
            is BooleanType -> true
            is DoubleType -> true
            is IntType -> true
            is LongType -> true

            // Unsupported:
            is CustomType,
            is EnumType,
            is ByteArrayType,
            is DoubleArrayType,
            is FloatArrayType,
            is IntArrayType,
            is ListType,
            is LongArrayType,
            is MapType,
            is StringType,
            is UnitType,
            is UndeterminedType,
            null -> false
        }
    }

    is MapType ->
        when(key) {
           is StringType,
           is IntType,
           is DoubleType,
           is BooleanType,
           is EnumType -> true
            // Unsupported:
            is CustomType,
            is ByteArrayType,
            is DoubleArrayType,
            is FloatArrayType,
            is IntArrayType,
            is ListType,
            is LongArrayType,
            is LongType,
            is MapType,
            is UnitType,
            is UndeterminedType,
            null -> false
        }
        // Unsupported:
    is UnitType,
    is UndeterminedType -> false
}