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

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import dev.buijs.klutter.compiler.processor.kcLogger
import dev.buijs.klutter.compiler.wrapper.KCController
import dev.buijs.klutter.compiler.wrapper.toKotlinClassWrapper
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.ast.Method
import dev.buijs.klutter.kore.common.EitherOk
import java.io.File

/**
 * FQDN for classes annotated with @Controller.
 */
private const val CONTROLLER_ANNOTATION = "dev.buijs.klutter.annotations.Controller"

/**
 * Get all classes with Controller annotation and convert them to [KCController].
 */
private fun getSymbolsWithResponseAnnotation(resolver: Resolver): List<KCController> =
    resolver.getSymbolsWithAnnotation(CONTROLLER_ANNOTATION)
        .filterIsInstance<KSClassDeclaration>()
        .map { clazz -> clazz.toKotlinClassWrapper() }
        .toList()

/**
 * Find all class annotated with @Controller.
 * </br>
 * Preliminary checks are done which result in either a [Controller] instance
 * or an error message describing the issue.
 * </br>
 * Full validation is done by [validateResponses()] and [validateControllers()]which takes the
 * full context (other Responses and/or Controllers) into consideration.
 */
@JvmOverloads
internal fun scanForControllers(
    outputFolder: File,
    resolver: Resolver,
    responses: Set<AbstractType>,
    scanner: (resolver: Resolver) -> List<KCController> = { getSymbolsWithResponseAnnotation(it) },
): List<ValidControllerOrError> =
    scanner.invoke(resolver)
        .map { it.toSquintMessageSourceOrFail(responses) }
        .toList()
        .also { it.writeOutput(outputFolder) }

private fun KCController.toSquintMessageSourceOrFail(
    responses: Set<AbstractType>
): Either<String, Controller> {

    if(!hasOneConstructor)
        return controllerHasTooManyConstructors()

    if(!firstConstructorHasNoParameters)
        return controllerIsMissingNoArgsConstructor()

    if(eventErrors.isNotEmpty())
        return controllerHasInvalidEvents(eventErrors)

    val controller = copy(
        events = events.map { event ->
            val requestDataTypeOrNull = event.requestDataType
            if(requestDataTypeOrNull == null) {
                kcLogger?.info("Controller (${this.className}) Event (${event.method}) has no request parameter.")
                event
            } else if(requestDataTypeOrNull !is UndeterminedType) {
                kcLogger?.info("Controller (${this.className}) Event (${event.method}) has request parameter of Type $requestDataTypeOrNull.")
                event
            } else {
                val normalizedDataTypeOrError = requestDataTypeOrNull.className.determineAbstractTypeOrFail(responses)
                if(normalizedDataTypeOrError is EitherOk) {
                    kcLogger?.info("Controller (${this.className}) Event (${event.method}) has request parameter of Type ${normalizedDataTypeOrError.data}.")
                    event.copy(requestDataType = normalizedDataTypeOrError.data)
                } else {
                    kcLogger?.info("Controller (${this.className}) Event (${event.method}) has request parameter of undetermined Type!")
                    event
                }
            }
        })

    return controller.toValidatedController(responses)
}

private fun KCController.toValidatedController(
    responses: Set<AbstractType>
): ValidControllerOrError = if(isBroadcastController) {
    toBroadcastController(
        typeParameter = broadcastTypeParameterOrBlank.determineAbstractTypeOrFail(responses),
        functions = events,
        type = controllerType)
} else {
    toSimpleController(functions = events, type = controllerType)
}

private fun String.determineAbstractTypeOrFail(
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

private fun KCController.toBroadcastController(
    typeParameter: Either<String,AbstractType>,
    functions: List<Method>,
    type: String)
: ValidControllerOrError {

    if(typeParameter is ValidAbstractType) {
        return when(type) {
            "RequestScoped" ->
                validRequestScopedBroadcastController(functions, typeParameter.data)
                    .also { kcLogger?.info("Executing [toBroadcastController]: $it") }
            else ->
                validSingletonBroadcastController(functions, typeParameter.data)
                    .also { kcLogger?.info("Executing [toBroadcastController]: $it") }
        }
    }

    return (typeParameter as InvalidAbstractType).data
        .broadcastControllerHasInvalidTypeParameterName()
        .also { kcLogger?.info("Encountered invalid TypeParameter during [toBroadcastController]: $it") }
}

private fun KCController.toSimpleController(
    functions: List<Method>, type: String
): ValidControllerOrError {
    return when(type) {
        "Singleton" ->
            validSingletonSimpleController(functions)
                .also { kcLogger?.info("Executing [toSimpleController]: $it") }
        else ->
            validRequestScopedSimpleController(functions)
                .also { kcLogger?.info("Executing [toSimpleController]: $it") }
    }
}