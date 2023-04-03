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
package dev.buijs.klutter.compiler.processor

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.SingletonBroadcastController
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.ast.RequestScopedController
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import dev.buijs.klutter.kore.shared.Method
import java.io.File

/**
 * FQDN for classes annotated with @Controller.
 */
private const val CONTROLLER_ANNOTATION = "dev.buijs.klutter.annotations.Controller"

/**
 * A Controller is invalid because its constructor fields.
 *
 * A Controller should only have a no-args constructor.
 */
private fun Any.missingNoArgsConstructor() = Either.nok<String, Controller>(
    "Controller $this is only allowed to have a no-arg constructor"
)

/**
 * A Controller is invalid because it has multiple constructors.
 */
private fun Any.tooManyConstructors() = Either.nok<String, Controller>(
    "Controller $this has multiple constructors but only 1 is allowed."
)

/**
 * Alias for [Either] with only right value of type [AbstractType].
 */
internal typealias ValidControllerType =
        EitherOk<String, Controller>

/**
 * Alias for [Either] with only left value of type [String] (error).
 */
internal typealias InvalidControllerType =
        EitherNok<String, Controller>
/**
 * Alias for [Either] with only right value of type [Method].
 */
internal typealias ValidAdaptee =
        EitherOk<String, Method>

/**
 * Alias for [Either] with only left value of type [String] (error).
 */
internal typealias InvalidAdaptee =
        EitherNok<String, Method>

/**
 * Process all class annotated with @KlutterResponse.
 */
internal fun Resolver.annotatedWithController(
    outputFolder: File
): List<Either<String, Controller>> =
    getSymbolsWithAnnotation(CONTROLLER_ANNOTATION)
        .filterIsInstance<KSClassDeclaration>()
        .map { it.toAbstractTypeOrFail() }
        .toList()
        .also { it.writeDebugOutput(outputFolder) }

/**
 * Write all AbstractTypes or error message to [outputFolder].
 */
private fun List<Either<String,Controller>>.writeDebugOutput(outputFolder: File) {

    var count = 0

    val folder = outputFolder
        .resolve("controller")
        .also { it.mkdir() }

    for(controllerOrError in this) {
        when (controllerOrError) {
            is EitherOk ->
                controllerOrError.toDebugOutput(count++) writeErrorIn folder
            is EitherNok ->
                controllerOrError.toDebugOutput(count++) writeErrorIn folder
        }
    }

}

/**
 * Return Pair:
 * - left: filename consisting of count + className
 * - right: file content consisting of class data
 */
private fun EitherOk<String,Controller>.toDebugOutput(count: Int) =
    Pair("${count}_${data.className.lowercase()}", data.toDebugString())

/**
 * Return Pair:
 * - left: filename consisting of count + '_invalid'
 * - right: file content consisting of error message
 */
private fun EitherNok<String,Controller>.toDebugOutput(count: Int) =
    Pair("${count}_invalid", data)

/**
 * Create a new file in [folder] and write debug content.
 */
private infix fun Pair<String, String>.writeErrorIn(folder: File) {
    folder.resolve("$first.txt").also {
        it.createNewFile()
        it.writeText(second)
    }
}

private fun KSClassDeclaration.toAbstractTypeOrFail(): Either<String, Controller> {

    if(getConstructors().toList().size != 1)
        return tooManyConstructors()

    if(getConstructors().first().parameters.isNotEmpty())
        return missingNoArgsConstructor()

    val functions = getAllFunctions().methods()

    if(functions.any { it is InvalidAdaptee })
        return InvalidControllerType(data = functions
            .filterIsInstance<InvalidAdaptee>()
            .joinToString { it.data },
        )

    val type = this.annotations
        .filter { it.shortName.asString() == "Controller" }
        .filter { it.arguments.isNotEmpty() }
        .map { it.arguments.firstOrNull { arg -> arg.name?.getShortName() == "type" } }
        .filterNotNull()
        .map { it.value.toString() }
        .firstOrNull { it.startsWith("dev.buijs.klutter.annotations.ControllerType.") }
        ?.substringAfterLast("dev.buijs.klutter.annotations.ControllerType.")
        ?: "Default"

    return toController(
        functions = functions.filterIsInstance<ValidAdaptee>().map { it.data },
        type = type
    )
}

private fun KSClassDeclaration.toController(
    functions: List<Method>,
    type: String
): Either<String, Controller> {

    val publisher = superTypes.firstOrNull { it.toString() == "Publisher" }

    if(publisher != null) {
        val resolved = publisher.resolve()
        val typeParameter = resolved.arguments.first().type?.toString() ?: ""
        val responseType = TypeData(typeParameter).toAbstractType()
        return if(responseType is ValidAbstractType) {
            when(type) {
                "RequestScoped" -> {
                    ValidControllerType(
                        data = RequestScopedBroadcastController(
                            packageName = packageName.asString(),
                            className = "$this",
                            functions = functions,
                            response = responseType.data
                        )
                    )
                }

                else -> {
                    ValidControllerType(
                        data = SingletonBroadcastController(
                            packageName = packageName.asString(),
                            className = "$this",
                            functions = functions,
                            response = responseType.data
                        )
                    )
                }
            }
        } else {
            InvalidControllerType(
                data = "Publisher has invalid TypeParameter: $typeParameter (not a KlutterResponse class)"
            )
        }
    }

    return when(type) {
        "Singleton" -> {
            ValidControllerType(
                data = SingletonController(
                    packageName = packageName.asString(),
                    className = "$this",
                    functions = functions,
                )
            )
        }

        else -> {
            ValidControllerType(
                data = RequestScopedController(
                    packageName = packageName.asString(),
                    className = "$this",
                    functions = functions,
                )
            )
        }
    }
}

private fun Sequence<KSAnnotated>.methods(): List<Either<String, Method>> = this
    .filter { it.isAdaptee() }
    .map { node ->
        val function = (node as KSFunctionDeclaration)
        function.returnType?.resolve().toAbstractFunctionType(function)

    }.toList()

private fun KSType?.toAbstractFunctionType(function: KSFunctionDeclaration): Either<String, Method> {
    if(this == null)
        return InvalidAdaptee(data = "KlutterAdaptee is missing a return value.")

    val command = function.getCommand()
        ?: return InvalidAdaptee(data = "KlutterAdaptee is missing value parameter 'name'.")

    val method = function.qualifiedName?.getShortName()
        ?: return InvalidAdaptee(data = "Unable to determine method signature of KlutterAdaptee.")

    val responseTypeOrError = TypeData(
        type = function.returnType?.toString() ?: "",
        arguments = arguments.map { it.type.toString() },
        nullable = isMarkedNullable
    ).toAbstractType()

    if (responseTypeOrError is InvalidAbstractType) {
        return InvalidAdaptee(responseTypeOrError.data)
    }

    val params = function.parameters

    if(params.size > 1)
        return InvalidAdaptee("Method has more than 1 parameter but only 0 or 1 is allowed.")

    val requestTypeOrErrorOrNull = params.firstOrNull()
        ?.type
        ?.toString()
        ?.let { TypeData(it).toAbstractType() }

    if (requestTypeOrErrorOrNull is InvalidAbstractType) {
        return InvalidAdaptee(requestTypeOrErrorOrNull.data)
    }

    return ValidAdaptee(
        data = Method(
            command = command,
            import = declaration.packageName.asString(),
            method = method,
            async = false, // TODO determine if false or true
            responseDataType = (responseTypeOrError as ValidAbstractType).data,
            requestDataType = requestTypeOrErrorOrNull?.let { (it as ValidAbstractType).data },
        )
    )
}

private fun KSFunctionDeclaration.getCommand() = annotations
    .firstOrNull { it.shortName.getShortName() == "KlutterAdaptee"}
    ?.arguments?.firstOrNull { it.name?.getShortName() == "name" }
    ?.value?.toString()

private fun KSAnnotated.isAdaptee() = annotations
    .map{ it.shortName.getShortName() }
    .toList()
    .contains("KlutterAdaptee")
