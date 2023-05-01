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
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.TypeData
import dev.buijs.klutter.kore.ast.toAbstractType
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * FQDN for classes annotated with @Response.
 */
private const val RESPONSE_ANNOTATION =
    "dev.buijs.klutter.annotations.Response"

/**
 * Alias for [Either] with only right value of type [SquintMessageSource].
 */
typealias ValidSquintType =
        EitherOk<String, SquintMessageSource>

/**
 * Alias for [Either] with only left value of type [String] (error).
 */
typealias InvalidSquintType =
        EitherNok<String, SquintMessageSource>

/**
 * Error indicating a class is missing the @Response annotation.
 */
private fun Any.missingSerializableAnnotation() =
    InvalidSquintType("Class is missing @Serializable annotation: $this")

/**
 * Error indicating a class does not extend KlutterJSON.
 */
private fun Any.doesNotExtendKlutterJSON() =
    InvalidSquintType("Class does not extend KlutterJSON: $this")

/**
 * A Response is invalid because it has multiple constructors.
 */
private fun Any.tooManyConstructors() = Either.nok<String, List<TypeMember>>(
    "Response $this has multiple constructors but only 1 is allowed."
)

/**
 * A Response is invalid because it has no constructor fields.
 */
private fun Any.emptyConstructor() = Either.nok<String, List<TypeMember>>(
    "Response $this constructor has no fields but atleast 1 is expected."
)

/**
 * Error indicating a field member is not immutable.
 */
private fun String.mutabilityError() =
    InvalidTypeMember("TypeMember is mutable: '$this'")

/**
 * Process all class annotated with @Response.
 */
internal fun Resolver.annotatedWithResponse(
    outputFolder: File
): List<Either<String, SquintMessageSource>> = getSymbolsWithAnnotation(RESPONSE_ANNOTATION)
    .filterIsInstance<KSClassDeclaration>()
    .toList()
    .map { it.toAbstractTypeOrFail() }
    .mapIndexed { index, either -> either.writeDebugOutput(outputFolder, index) }

/**
 * Write all AbstractTypes or error message to [outputFolder].
 */
private fun Either<String,SquintMessageSource>.writeDebugOutput(
    outputFolder: File,
    count: Int,
    ): Either<String,SquintMessageSource> {

    val folder = outputFolder
        .resolve("response")
        .also { it.mkdir() }

    if(this is EitherNok) {
        toDebugOutput(count) writeErrorIn folder
        return this
    }

    val type = this as EitherOk
    val source = type.toDebugOutput() writeContentIn folder
    return ValidSquintType(data = data.copy(source = source))

}

/**
 * Return Pair:
 * - left: filename consisting className
 * - right: file content consisting of class data
 */
private fun EitherOk<String,SquintMessageSource>.toDebugOutput() =
    Pair(data.type.className.lowercase(), data)

/**
 * Return Pair:
 * - left: filename consisting of count + '_invalid'
 * - right: file content consisting of error message
 */
private fun EitherNok<String,SquintMessageSource>.toDebugOutput(count: Int) =
    Pair("${count}_invalid", data)

/**
 * Create a new file in [folder] and write debug content.
 */
private infix fun Pair<String, String>.writeErrorIn(folder: File) =
    folder.resolve("sqdb_$first.json").also {
        it.createNewFile()
        it.writeText(second)
    }

/**
 * Create a new file in [folder] and write debug content.
 */
private infix fun Pair<String, SquintMessageSource>.writeContentIn(folder: File) =
    folder.resolve("sqdb_$first.json").also {
        it.createNewFile()
        when (second.squintType) {
            is SquintCustomType ->
                it.writeText(Json.encodeToString(second.squintType as SquintCustomType))
            is SquintEnumType ->
                it.writeText(Json.encodeToString(second.squintType as SquintEnumType))
        }
    }

private fun KSClassDeclaration.toAbstractTypeOrFail(): Either<String, SquintMessageSource> {

    val annotations = annotations
        .map{ it.shortName.getShortName() }
        .toList()

    if(!annotations.contains("Serializable"))
        return missingSerializableAnnotation()

    if(classKind.type.trim().lowercase() == "enum_class")
        return enumeration()

    if(!superTypes.map { it.toString() }.toList().contains("KlutterJSON"))
        return doesNotExtendKlutterJSON()

    val fields = getConstructorFields()

    return if(fields is EitherNok) {
        InvalidSquintType("$this is invalid: ${fields.data}")
    } else {
        val className = "$this"

        val type = CustomType(
            className = className,
            packageName = this.packageName.asString(),
            members = (fields as EitherOk).data,
        )

        val squintType = SquintCustomType(
            className = className,
            members = fields.data.map {
                SquintCustomTypeMember(
                    name = it.name,
                    type = it.type.let { t -> t.typeSimplename(asKotlinType = false) },
                    nullable = it.type is Nullable
                )
            },
        )

        ValidSquintType(
            data = SquintMessageSource(
                type = type,
                squintType = squintType,
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

private fun KSClassDeclaration.enumeration(): Either<String, SquintMessageSource> {

    val className = "$this"

    val members = declarations
        .filter { (it.qualifiedName?.getShortName() ?: "") != "<init>" }
        .filterIsInstance<KSClassDeclaration>()

    val values = members
        .map { it.qualifiedName?.getShortName() ?: ""}
        .toList()

    val valuesJSON = members
        .map { it.annotations }
        .map { it.firstOrNull { annotation -> annotation.shortName.getShortName() == "SerialName" } }
        .map { it?.arguments?.firstOrNull() }
        .map { it?.value.toString() }
        .toList()

    val enumType = EnumType(
        className = className,
        packageName = packageName.asString(),
        values = values,
        valuesJSON = valuesJSON
    )

    val squintType = SquintEnumType(
        className = className,
        values = values,
        valuesJSON = valuesJSON
    )

    return Either.ok(
        data = SquintMessageSource(
            type = enumType,
            squintType = squintType,
        )
    )
}