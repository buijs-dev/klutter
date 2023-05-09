package dev.buijs.klutter.compiler.wrapper

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import dev.buijs.klutter.compiler.scanner.eventHasTooManyParameters
import dev.buijs.klutter.compiler.scanner.eventHasUndeterminedMethodSignature
import dev.buijs.klutter.compiler.scanner.eventIsMissingParameter
import dev.buijs.klutter.compiler.scanner.eventIsMissingReturnValue
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.ExcludeFromJacocoGeneratedReport

/**
 * Wrapper for KSP KSAnnotated to encapsulate KSP specific classes.
 */
internal data class KAWrapper(
    val hasEventAnnotation: Boolean,
    val errorMessageOrNull: String? = null,
    val method: Method? = null)

@ExcludeFromJacocoGeneratedReport(
    reason = "Requires way too much mocking/stubbing. Is test through module test-ksp.")
internal fun KSAnnotated.toKAWrapper(): KAWrapper {
    if(!hasEventAnnotation())
        return KAWrapper(hasEventAnnotation = false)

    if(this is KSFunctionDeclaration)
        return this.toKAWrapper()

    return KAWrapper(hasEventAnnotation = true)
}

@ExcludeFromJacocoGeneratedReport(
    reason = "Requires way too much mocking/stubbing. Is test through module test-ksp.")
private fun KSFunctionDeclaration.toKAWrapper(): KAWrapper {

    val ksType = returnType?.resolve()
        ?: return eventIsMissingReturnValue.responseTypeError()

    val responseType = ksType.toTypeData(this).toAbstractType()

    if(responseType is InvalidAbstractType)
        return responseType.data.responseTypeError()

    if(parameters.size > 1)
        return eventHasTooManyParameters.responseTypeError()

    val requestParameterOrNull = parameters.firstOrNull()

    val requestTypeOrErrorOrNull = requestParameterOrNull
        ?.type.toString().let { TypeData(it).toAbstractType() }

    if (requestTypeOrErrorOrNull is InvalidAbstractType)
        return requestTypeOrErrorOrNull.data.requestTypeError()

    val commandName = getCommand()
        ?: return commandNameError()

    val methodName = qualifiedName?.getShortName()
        ?: return methodSignatureError()

    return KAWrapper(
        hasEventAnnotation = true,
        method = Method(
            command = commandName,
            import = ksType.declaration.packageName.asString(),
            method = methodName,
            async = modifiers.map { "$it" }.any { it == "SUSPEND" },
            responseDataType = (responseType as ValidAbstractType).data,
            requestDataType = requestTypeOrErrorOrNull.let { (it as ValidAbstractType).data },
            requestParameterName = requestParameterOrNull?.name?.getShortName()))
}

@ExcludeFromJacocoGeneratedReport
private fun String.responseTypeError() =
    KAWrapper(hasEventAnnotation = true, errorMessageOrNull = this)

@ExcludeFromJacocoGeneratedReport
private fun String.requestTypeError() =
    KAWrapper(hasEventAnnotation = true, errorMessageOrNull = this)

@ExcludeFromJacocoGeneratedReport
private fun commandNameError() =
    KAWrapper(hasEventAnnotation = true, errorMessageOrNull = eventIsMissingParameter)

@ExcludeFromJacocoGeneratedReport
private fun methodSignatureError() =
    KAWrapper(hasEventAnnotation = true, errorMessageOrNull = eventHasUndeterminedMethodSignature)

@ExcludeFromJacocoGeneratedReport
private fun KSType.toTypeData(function: KSFunctionDeclaration) = TypeData(
    type = function.returnType?.toString() ?: "",
    arguments = arguments.map { it.type.toString() },
    nullable = isMarkedNullable)

@ExcludeFromJacocoGeneratedReport
private fun KSFunctionDeclaration.getCommand() = annotations
    .firstOrNull { it.shortName.getShortName() == "Event"}
    ?.arguments?.firstOrNull { it.name?.getShortName() == "name" }
    ?.value?.toString()

@ExcludeFromJacocoGeneratedReport
private fun KSAnnotated.hasEventAnnotation(): Boolean = annotations
    .map{ it.shortName.getShortName() }
    .toList()
    .contains("Event")