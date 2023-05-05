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

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.Either

/**
 * Find all methods annotated with @Event.
 */
internal fun Sequence<KSAnnotated>.getEvents(): List<Either<String, Method>> = this
    .filter { it.isAnnotatedWithEvent() }
    .map {
        val function = (it as KSFunctionDeclaration)
        function.returnType?.resolve().toAbstractFunctionType(function)
    }.toList()

private fun KSType?.toAbstractFunctionType(function: KSFunctionDeclaration): Either<String, Method> {
    if(this == null)
        return eventIsMissingReturnValue

    val command = function.getCommand()
        ?: return eventIsMissingParameter

    val method = function.qualifiedName?.getShortName()
        ?: return eventHasUndeterminedMethodSignature

    val responseTypeOrError =
        toTypeData(function).toAbstractType()

    if (responseTypeOrError is InvalidAbstractType)
        return InvalidEvent(responseTypeOrError.data)

    val params = function.parameters

    if(params.size > 1)
        return eventHasTooManyParameters

    val requestParameterOrNull = params.firstOrNull()

    val requestTypeOrErrorOrNull = requestParameterOrNull
        ?.toAbstractTypeOrError()

    if (requestTypeOrErrorOrNull is InvalidAbstractType)
        return InvalidEvent(requestTypeOrErrorOrNull.data)

    return ValidEvent(Method(
        command = command,
        import = declaration.packageName.asString(),
        method = method,
        async = function.modifiers.map { "$it"}.any { it == "SUSPEND" },
        responseDataType = (responseTypeOrError as ValidAbstractType).data,
        requestDataType = requestTypeOrErrorOrNull?.let { (it as ValidAbstractType).data },
        requestParameterName = requestParameterOrNull?.name?.getShortName()))
}

private fun KSType.toTypeData(function: KSFunctionDeclaration) = TypeData(
    type = function.returnType?.toString() ?: "",
    arguments = arguments.map { it.type.toString() },
    nullable = isMarkedNullable)

private fun KSValueParameter.toAbstractTypeOrError(): Either<String, AbstractType> =
    type.toString().let { TypeData(it).toAbstractType() }

private fun KSFunctionDeclaration.getCommand() = annotations
    .firstOrNull { it.shortName.getShortName() == "Event"}
    ?.arguments?.firstOrNull { it.name?.getShortName() == "name" }
    ?.value?.toString()

private fun KSAnnotated.isAnnotatedWithEvent() = annotations
    .map{ it.shortName.getShortName() }
    .toList()
    .contains("Event")
