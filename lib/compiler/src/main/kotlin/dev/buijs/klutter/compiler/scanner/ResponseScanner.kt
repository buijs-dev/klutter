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
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.TypeData
import dev.buijs.klutter.kore.ast.toAbstractType
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import java.io.File

/**
 * FQDN for classes annotated with @Response.
 */
private const val RESPONSE_ANNOTATION =
    "dev.buijs.klutter.annotations.Response"

/**
 * Process all class annotated with @Response.
 */
internal fun Resolver.annotatedWithResponse(
    outputFolder: File
): List<Either<String, SquintMessageSource>> = getSymbolsWithAnnotation(RESPONSE_ANNOTATION)
    .filterIsInstance<KSClassDeclaration>()
    .toList()
    .map { it.toAbstractTypeOrFail() }
    .mapIndexed { index, either -> either.writeOutput(outputFolder, index) }

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
            members = (fields as EitherOk).data)

        val squintType = SquintCustomType(
            className = className,
            members = fields.data.map {
                SquintCustomTypeMember(
                    name = it.name,
                    type = it.type.let { t -> t.typeSimplename(asKotlinType = false) },
                    nullable = it.type is Nullable) })

        ValidSquintType(SquintMessageSource(type = type, squintType = squintType))
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
        else -> responseHasTooManyConstructors()
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