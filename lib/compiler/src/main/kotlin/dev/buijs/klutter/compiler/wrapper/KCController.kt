package dev.buijs.klutter.compiler.wrapper

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import dev.buijs.klutter.compiler.scanner.InvalidEvent
import dev.buijs.klutter.compiler.scanner.ValidEvent
import dev.buijs.klutter.compiler.scanner.getEvents
import dev.buijs.klutter.kore.ast.Method
import dev.buijs.klutter.kore.common.ExcludeFromJacocoGeneratedReport

/**
 * Wrapper for KSP KSClassDeclaration to encapsulate KSP specific classes.
 */
internal data class KCController(
    val hasOneConstructor: Boolean,
    val firstConstructorHasNoParameters: Boolean,
    val methods: List<Method>,
    val eventErrors: List<String>,
    val controllerType: String,
    val isBroadcastController: Boolean,
    val broadcastTypeParameterOrBlank: String,
    val className: String,
    val packageName: String)

@ExcludeFromJacocoGeneratedReport(
    reason = "Requires way too much mocking/stubbing. Is test through module test-ksp.")
internal fun KSClassDeclaration.toKotlinClassWrapper(): KCController {

    val constructors: List<KSFunctionDeclaration> = getConstructors().toList()

    val controllerType = annotations
        .filter { it.shortName.asString() == "Controller" }
        .filter { it.arguments.isNotEmpty() }
        .map { it.arguments.firstOrNull { arg -> arg.name?.getShortName() == "type" } }
        .filterNotNull()
        .map { it.value.toString() }
        .firstOrNull { it.startsWith("dev.buijs.klutter.annotations.ControllerType.") }
        ?.substringAfterLast("dev.buijs.klutter.annotations.ControllerType.")
        ?: "Default"

    val publisherOrNull =
        superTypes.firstOrNull { it.toString() == "Publisher" }

    val events = getAllFunctions().map { it.toKAWrapper() }.getEvents()

    return KCController(
        hasOneConstructor = constructors.size == 1,
        firstConstructorHasNoParameters = constructors.firstOrNull()?.parameters?.isEmpty() ?: false,
        methods = events.filterIsInstance<ValidEvent>().map { it.data },
        eventErrors = events.filterIsInstance<InvalidEvent>().map { it.data },
        controllerType = controllerType,
        isBroadcastController = publisherOrNull != null,
        broadcastTypeParameterOrBlank = publisherOrNull?.resolve()?.arguments?.first()?.type?.toString() ?: "",
        className ="$this",
        packageName = packageName.asString())
}