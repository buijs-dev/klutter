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
package dev.buijs.klutter.kore.processor

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.TypeData
import dev.buijs.klutter.kore.ast.toAbstractType
import dev.buijs.klutter.kore.utils.Either
import dev.buijs.klutter.kore.utils.EitherNok
import dev.buijs.klutter.kore.utils.EitherOk
import java.io.File

/**
 * FQDN for classes annotated with @KlutterResponse.
 */
private const val klutterResponseAnnotation =
    "dev.buijs.klutter.annotations.KlutterResponse"

/**
 * Error indicating a class is missing the @KlutterResponse annotation.
 */
private fun Any.missingSerializableAnnotation() =
    InvalidAbstractType("Class is missing @Serializable annotation: $this")

/**
 * Error indicating a class does not extend KlutterJSON.
 */
private fun Any.doesNotExtendKlutterJSON() =
    InvalidAbstractType("Class does not extend KlutterJSON: $this")

/**
 * A KlutterResponse is invalid because it has multiple constructors.
 */
private fun Any.tooManyConstructors() = Either.nok<String, List<TypeMember>>(
    "KlutterResponse $this has multiple constructors but only 1 is allowed."
)

/**
 * A KlutterResponse is invalid because it has no constructor fields.
 */
private fun Any.emptyConstructor() = Either.nok<String, List<TypeMember>>(
    "KlutterResponse $this constructor has no fields but atleast 1 is expected."
)

/**
 * Error indicating a field member is not immutable.
 */
private fun String.mutabilityError() =
    InvalidTypeMember("TypeMember is mutable: '$this'")

/**
 * Process all class annotated with @KlutterResponse.
 */
internal fun Resolver.annotatedWithKlutterResponse(
    outputFolder: File
): List<Either<String, AbstractType>> = getSymbolsWithAnnotation(klutterResponseAnnotation)
    .filterIsInstance<KSClassDeclaration>()
    .toList()
    .map { it.toAbstractTypeOrFail() }
    .also { it.writeDebugOutput(outputFolder) }

/**
 * Write all AbstractTypes or error message to [outputFolder].
 */
private fun List<Either<String,AbstractType>>.writeDebugOutput(outputFolder: File) {

    var count = 0

    val folder = outputFolder
        .resolve("klutter_response")
        .also { it.mkdir() }

    for(typeOrError in this) {
        when (typeOrError) {
            is EitherOk ->
                typeOrError.toDebugOutput(count++) writeIn folder
            is EitherNok ->
                typeOrError.toDebugOutput(count++) writeIn folder
        }
    }

}

/**
 * Return Pair:
 * - left: filename consisting of count + className
 * - right: file content consisting of class data
 */
private fun EitherOk<String,AbstractType>.toDebugOutput(count: Int) =
    Pair("${count}_${data.className.lowercase()}", data.toString())

/**
 * Return Pair:
 * - left: filename consisting of count + '_invalid'
 * - right: file content consisting of error message
 */
private fun EitherNok<String,AbstractType>.toDebugOutput(count: Int) =
    Pair("${count}_invalid", data)

/**
 * Create a new file in [folder] and write debug content.
 */
private infix fun Pair<String, String>.writeIn(folder: File) {
    folder.resolve("$first.txt").also {
        it.createNewFile()
        it.writeText(second)
    }
}

private fun KSClassDeclaration.toAbstractTypeOrFail(): Either<String, AbstractType> {

    val annotations = annotations
        .map{ it.shortName.getShortName() }
        .toList()

    if(!annotations.contains("Serializable"))
        return annotations.joinToString { it }.missingSerializableAnnotation()

    if(!superTypes.map { it.toString() }.toList().contains("KlutterJSON"))
        return doesNotExtendKlutterJSON()

    val fields = getConstructorFields()

    return if(fields is EitherNok) {
        InvalidAbstractType("$this is invalid: ${fields.data}")
    } else {
        ValidAbstractType(
            data = CustomType(
                className = "$this",
                packageName = this.packageName.asString(),
                fields = (fields as EitherOk).data,
            )
        )
    }

}

private fun KSClassDeclaration.getConstructorFields(): Either<String, List<TypeMember>> {
    val constructors = getConstructors().toList()
    return when(constructors.size) {
        0 -> emptyConstructor()
        1 -> {
            val members = constructors
                .first()
                .getTypeMembers()

            val valid = members
                .filterIsInstance<ValidTypeMember>()

            val invalid = members
                .filterIsInstance<InvalidTypeMember>()
                .map { it.data }

            if(invalid.isEmpty()) {
                Either.ok(valid.map { it.data })
            } else {
                Either.nok(invalid.joinToString { it })
            }
        }
        else -> tooManyConstructors()
    }
}

private fun KSFunctionDeclaration.getTypeMembers() = parameters.map { param ->
    val name = param.name?.getShortName()

    val resolved = param.type.resolve()

    when {
        name == null ->
            InvalidTypeMember("Unable to determine $this field name.")

        !param.isVal ->
            name.mutabilityError()

        else -> {
            val maybeType = TypeData(
                type = param.type.toString().trim(),
                arguments = resolved.arguments.map { it.type.toString() },
                nullable = resolved.isMarkedNullable,
            ).toAbstractType()

            if(maybeType is EitherOk) {
                ValidTypeMember(
                    data = TypeMember(
                        name = name,
                        type = maybeType.data,
                    )
                )
            } else {
                InvalidTypeMember(
                    data = (maybeType as EitherNok).data
                )
            }
        }

    }

}