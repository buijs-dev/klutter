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

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
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
 * Find all class annotated with @Controller.
 * </br>
 * Preliminary checks are done which result in either a [Controller] instance
 * or an error message describing the issue.
 * </br>
 * Full validation is done by [validateResponses()] which takes the
 * full context (other Responses and/or Controllers) into consideration.
 */
internal fun Resolver.annotatedWithController(outputFolder: File): List<ValidControllerOrError> =
    getSymbolsWithAnnotation(CONTROLLER_ANNOTATION)
        .filterIsInstance<KSClassDeclaration>()
        .map { it.toAbstractTypeOrFail() }
        .toList()
        .also { it.writeOutput(outputFolder) }

private fun KSClassDeclaration.toAbstractTypeOrFail(): Either<String, Controller> {

    if(getConstructors().toList().size != 1)
        return controllerHasTooManyConstructors()

    if(getConstructors().first().parameters.isNotEmpty())
        return controllerIsMissingNoArgsConstructor()

    val functions = getAllFunctions().getEvents()

    if(functions.any { it is InvalidEvent })
        return functions.filterIsInstance<InvalidEvent>().controllerHasInvalidEvents()

    val type = determineControllerType()

    val validFunctions =
        functions.filterIsInstance<ValidEvent>().map { it.data }

    return toController(functions = validFunctions, type = type)
}

private fun KSClassDeclaration.determineControllerType() = this
    .annotations
    .filter { it.shortName.asString() == "Controller" }
    .filter { it.arguments.isNotEmpty() }
    .map { it.arguments.firstOrNull { arg -> arg.name?.getShortName() == "type" } }
    .filterNotNull()
    .map { it.value.toString() }
    .firstOrNull { it.startsWith("dev.buijs.klutter.annotations.ControllerType.") }
    ?.substringAfterLast("dev.buijs.klutter.annotations.ControllerType.")
    ?: "Default"

private fun KSClassDeclaration.toController(functions: List<Method>, type: String): ValidControllerOrError {
    val publisher = superTypes.firstOrNull { it.toString() == "Publisher" }
        ?: return toSimpleController(functions, type)
    return toBroadcastController(publisher.resolve(), functions, type)
}

private fun KSClassDeclaration.toBroadcastController(
    publisher: KSType, functions: List<Method>, type: String
): ValidControllerOrError {

    val typeParameter =
        publisher.arguments.first().type?.toString() ?: ""

    val responseType =
        TypeData(typeParameter).toAbstractType()

    if(responseType is ValidAbstractType) {
        return when(type) {
            "RequestScoped" ->
                validRequestScopedBroadcastController(functions, responseType.data)
            else ->
                validSingletonBroadcastController(functions,responseType.data)
        }
    }
    return typeParameter.publisherControllerHasInvalidTypeParameter()
}

private fun KSClassDeclaration.toSimpleController(
    functions: List<Method>, type: String
): ValidControllerOrError {
    return when(type) {
        "Singleton" ->
            validSingletonSimpleController(functions)
        else ->
            validRequestScopedSimpleController(functions)
    }
}