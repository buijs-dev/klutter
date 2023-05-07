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
import dev.buijs.klutter.compiler.wrapper.KCWrapper
import dev.buijs.klutter.compiler.wrapper.toKotlinClassWrapper
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.ast.Method
import java.io.File

/**
 * FQDN for classes annotated with @Controller.
 */
private const val CONTROLLER_ANNOTATION = "dev.buijs.klutter.annotations.Controller"

/**
 * Get all classes with Controller annotation and convert them to [KCWrapper].
 */
private fun getSymbolsWithControllerAnnotation(resolver: Resolver): List<KCWrapper> =
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
 * Full validation is done by [validateResponses()] which takes the
 * full context (other Responses and/or Controllers) into consideration.
 */
@JvmOverloads
internal fun scanForControllers(
    outputFolder: File,
    resolver: Resolver,
    scanner: (resolver: Resolver) -> List<KCWrapper> = { getSymbolsWithControllerAnnotation(it) },
): List<ValidControllerOrError> =
    scanner.invoke(resolver)
        .map { it.toAbstractTypeOrFail() }
        .toList()
        .also { it.writeOutput(outputFolder) }

private fun KCWrapper.toAbstractTypeOrFail(): Either<String, Controller> {

    if(!hasOneConstructor)
        return controllerHasTooManyConstructors()

    if(!firstConstructorHasNoParameters)
        return controllerIsMissingNoArgsConstructor()

    if(eventErrors.isNotEmpty())
        return controllerHasInvalidEvents(eventErrors)

    return toController()
}

private fun KCWrapper.toController(): ValidControllerOrError = if(isBroadcastController) {
    toBroadcastController(broadcastTypeParameterOrBlank, methods, controllerType)
} else {
    toSimpleController(methods, controllerType)
}

private fun KCWrapper.toBroadcastController(
    typeParameter: String, functions: List<Method>, type: String)
: ValidControllerOrError {

    val responseType = TypeData(typeParameter).toAbstractType()

    if(responseType is ValidAbstractType) {
        return when(type) {
            "RequestScoped" ->
                validRequestScopedBroadcastController(functions, responseType.data)
            else ->
                validSingletonBroadcastController(functions,responseType.data)
        }
    }

    return typeParameter.broadcastControllerHasInvalidTypeParameterName()
}

private fun KCWrapper.toSimpleController(
    functions: List<Method>, type: String
): ValidControllerOrError {
    return when(type) {
        "Singleton" ->
            validSingletonSimpleController(functions)
        else ->
            validRequestScopedSimpleController(functions)
    }
}