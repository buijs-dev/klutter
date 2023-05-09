package dev.buijs.klutter.compiler.wrapper

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import dev.buijs.klutter.compiler.scanner.*
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import dev.buijs.klutter.kore.common.ExcludeFromJacocoGeneratedReport

/**
 * Wrapper for KSP KSClassDeclaration to encapsulate KSP specific classes.
 * </br>
 * Get all information from the KSClassDeclaration which is required to map to SquintMessageSource.
 */
internal sealed interface KCResponse {
    val isSerializableAnnotated: Boolean
    val className: String
    val packageName: String
}

internal data class KCEnumeration(
    override val isSerializableAnnotated: Boolean,
    override val className: String,
    override val packageName: String,
    val values: List<String>,
    val valuesJSON: List<String>,
): KCResponse

internal data class KCMessage(
    override val isSerializableAnnotated: Boolean,
    override val className: String,
    override val packageName: String,
    val extendsKlutterJSON: Boolean,
    val hasOneConstructor: Boolean,
    val typeMembers: List<Either<String, TypeMember>>
): KCResponse

@ExcludeFromJacocoGeneratedReport(
    reason = "Requires way too much mocking/stubbing. Is test through module test-ksp.")
internal fun KSClassDeclaration.toKCResponse(): KCResponse {

    val annotationNames = annotations
        .map{ it.shortName.getShortName() }
        .toList()

    val isSerializableAnnotated =
        annotationNames.contains("Serializable")

    val isEnumeration =
        classKind.type.trim().lowercase() == "enum_class"

    if(isEnumeration) {
        val members = declarations
            .filter { (it.qualifiedName?.getShortName() ?: "") != "<init>" }
            .filterIsInstance<KSClassDeclaration>()

        return KCEnumeration(
            isSerializableAnnotated = isSerializableAnnotated,
            className ="$this",
            packageName = packageName.asString(),
            values = members.map { it.qualifiedName?.getShortName() ?: ""}.toList(),
            valuesJSON = members
                .map { it.annotations }
                .map { it.firstOrNull { annotation -> annotation.shortName.getShortName() == "SerialName" } }
                .map { it?.arguments?.firstOrNull() }
                .map { it?.value.toString() }
                .toList())
    }

    val extendsKlutterJSON = superTypes.map { it.toString() }.toList().contains("KlutterJSON")

    val constructors: List<KSFunctionDeclaration> = getConstructors().toList()

    return KCMessage(
        isSerializableAnnotated = isSerializableAnnotated,
        extendsKlutterJSON = extendsKlutterJSON,
        className ="$this",
        packageName = packageName.asString(),
        hasOneConstructor = constructors.size == 1,
        typeMembers =  constructors.firstOrNull()?.getTypeMembers() ?: emptyList())
}

@ExcludeFromJacocoGeneratedReport(
    reason = "Requires way too much mocking/stubbing. Is test through module test-ksp.")
private fun KSFunctionDeclaration.getTypeMembers() = parameters.map { param ->
    val name = param.name?.getShortName()

    val resolved = param.type.resolve()

    if (name == null)
        InvalidTypeMember("Unable to determine $this field name.")

    else if(!param.isVal)
        name.mutabilityError()

    else {
        val maybeType = TypeData(
            type = param.type.toString().trim(),
            arguments = resolved.arguments.map { it.type.toString() },
            nullable = resolved.isMarkedNullable)
            .toAbstractType()

        if(maybeType is EitherOk) {
            ValidTypeMember(TypeMember(name = name, type = maybeType.data))
        } else {
            InvalidTypeMember(data = (maybeType as EitherNok).data)
        }
    }

}