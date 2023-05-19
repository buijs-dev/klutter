package dev.buijs.klutter.compiler.wrapper

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import dev.buijs.klutter.compiler.processor.kcLogger
import dev.buijs.klutter.compiler.scanner.eventHasTooManyParameters
import dev.buijs.klutter.compiler.scanner.eventHasUndeterminedMethodSignature
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
    reason = "Requires way too much mocking/stubbing.")
internal fun KSAnnotated.toKAWrapper(): KAWrapper {
    if(!hasEventAnnotation())
        return KAWrapper(hasEventAnnotation = false)

    if(this is KSFunctionDeclaration)
        return this.toKAWrapper()

    return KAWrapper(hasEventAnnotation = true)
}

@ExcludeFromJacocoGeneratedReport(
    reason = "Requires way too much mocking/stubbing.")
private fun KSFunctionDeclaration.toKAWrapper(): KAWrapper {

    val ksType = returnType?.resolve()
        ?: return eventIsMissingReturnValue.responseTypeError()

    val responseType = ksType.toTypeData(this).toStandardTypeOrUndetermined()

    if(responseType is InvalidAbstractType)
        return responseType.data.responseTypeError()

    if(parameters.size > 1)
        return eventHasTooManyParameters.responseTypeError()

    val requestParameterOrNull = parameters.firstOrNull()

    val requestParameterTypeOrErrorOrNull = requestParameterOrNull
        ?.type
        ?.resolve()
        ?.toString()
        ?.also { kcLogger?.info("Found request type parameter: $it") }
        ?.let { TypeData(it) }
        ?.also { kcLogger?.info("Mapped request type parameter to TypeData: $it") }
        ?.toStandardTypeOrUndetermined()
        ?.also { kcLogger?.info("Mapped request type parameter to AbstractType: $it") }

    if (requestParameterTypeOrErrorOrNull is InvalidAbstractType)
        return requestParameterTypeOrErrorOrNull.data.requestTypeError()

    val methodName = qualifiedName?.getShortName()
        ?: return methodSignatureError()

    kcLogger?.info("Event has methodName: '$methodName'")

    val commandName = getCommandOrUseMethodName(methodName)

    kcLogger?.info("Event has commandName: '$commandName'")

    return KAWrapper(
        hasEventAnnotation = true,
        method = Method(
            command = commandName,
            import = ksType.declaration.packageName.asString(),
            method = methodName,
            async = modifiers.map { "$it" }.any { it == "SUSPEND" },
            responseDataType = (responseType as ValidAbstractType).data,
            requestDataType = requestParameterTypeOrErrorOrNull?.let { (it as ValidAbstractType).data },
            requestParameterName = requestParameterOrNull?.name?.getShortName()))
}

@ExcludeFromJacocoGeneratedReport
private fun String.responseTypeError() =
    KAWrapper(hasEventAnnotation = true, errorMessageOrNull = this)

@ExcludeFromJacocoGeneratedReport
private fun String.requestTypeError() =
    KAWrapper(hasEventAnnotation = true, errorMessageOrNull = this)

@ExcludeFromJacocoGeneratedReport
private fun methodSignatureError() =
    KAWrapper(hasEventAnnotation = true, errorMessageOrNull = eventHasUndeterminedMethodSignature)

@ExcludeFromJacocoGeneratedReport
private fun KSType.toTypeData(function: KSFunctionDeclaration) = TypeData(
    type = function.returnType?.toString() ?: "",
    arguments = arguments.map { it.type.toString() },
    nullable = isMarkedNullable)

@ExcludeFromJacocoGeneratedReport
private fun KSFunctionDeclaration.getCommandOrUseMethodName(methodName: String): String {
    val event = annotations.firstOrNull { it.shortName.getShortName() == "Event"}
    return event.getEventNamedArgumentOrNull()
        ?: event.getEventArgumentOrNull()
        ?: methodName
}

@ExcludeFromJacocoGeneratedReport
private fun KSAnnotated.hasEventAnnotation(): Boolean = annotations
    .map{ it.shortName.getShortName() }
    .toList()
    .contains("Event")

@ExcludeFromJacocoGeneratedReport
private fun KSAnnotation?.getEventNamedArgumentOrNull(): String? = this
    ?.arguments
    ?.firstOrNull { it.name?.getShortName() == "name" }
    ?.value
    ?.toString()
    ?.ifBlank { null }
    ?.also { kcLogger?.info("Inside getEventNamedArgumentOrNull: '$it'") }

@ExcludeFromJacocoGeneratedReport
private fun KSAnnotation?.getEventArgumentOrNull(): String? = this
    ?.arguments
    ?.firstNotNullOfOrNull { it.value?.toString() }
    ?.also { kcLogger?.info("Inside getEventArgumentOrNull: '$it'") }