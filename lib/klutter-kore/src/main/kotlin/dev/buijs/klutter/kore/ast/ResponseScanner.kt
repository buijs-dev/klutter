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
package dev.buijs.klutter.kore.ast

import dev.buijs.klutter.kore.utils.Either
import dev.buijs.klutter.kore.utils.EitherNok
import dev.buijs.klutter.kore.utils.EitherOk
import java.io.File

/**
 * Regex to find al valid KlutterResponse definitions.
 */
private val klutterJsonRegex = ("""""" +
        """((^@Serializable$|^@KlutterResponse$)*.+?)""" +
        """open.+?class\s+(.*?[^(]+?)""" +
        """\(([^\)]*?""" +
        """(val\s*([^:]+?):\s*([^,\)]+)(,|\)))*""" +
        """)\)""" +
        """:\s+?KlutterJSON<([^>]+?)>"""
        ).toRegex()

/**
 * Alias for [Either] with only right value of type List of [CustomType].
 */
internal typealias KlutterResponseScanningOk =
        EitherOk<List<String>, List<AbstractType>>

/**
 * Alias for [Either] with only left value of type List of [String] (error).
 */
internal typealias KlutterResponseScanningNok =
        EitherNok<List<String>, List<AbstractType>>

/**
 * Response for [findKlutterResponses].
 */
internal typealias KlutterResponseScanning =
        Either<List<String>, List<AbstractType>>

/**
 * Error indicating a class is missing the @KlutterResponse annotation.
 */
private fun missingKlutterResponseAnnotation(name: String) =
    InvalidAbstractType("Class is missing @KlutterResponse annotation: $name")

/**
 * Error indicating a class is missing the @KlutterResponse annotation.
 */
private fun missingSerializableAnnotation(name: String) =
    InvalidAbstractType("Class is missing @Serializable annotation: $name")

/**
 * Error indicating a KlutterResponse class has no members.
 */
private fun emptyKlutterResponseError(types: List<String>) = KlutterResponseScanningNok(
    listOf("KlutterResponse contract violation! Some classes have no fields: $types")
)

/**
 * Error indicating multiple KlutterResponse classes have the same class name.
 */
private fun duplicateKlutterResponseError(types: List<String>) = KlutterResponseScanningNok(
    listOf("KlutterResponse contract violation! Duplicate class names: $types")
)

/**
 * Error indicating a KlutterResponse class has a TypeMember of Unknown CustomType.
 */
private fun unknownKlutterResponseError(types: List<String>) = KlutterResponseScanningNok(
    listOf("Unknown KlutterResponse TypeMember: $types")
)

/**
 * Error indicating one or more errors occured while processing a KlutterResponse class.
 */
private fun processingError(msg: String) =
    InvalidAbstractType("KlutterResponse class could not be processed: $msg")

/**
 * Find all valid KlutterResponse classes.
 * - List of KlutterResponse [AbstractType] if all are valid
 * - List of [String] errors if not.
 */
fun List<File>.findKlutterResponses(): KlutterResponseScanning {

    val result = this
        .map { it to it.packageName() }
        .flatMap { it.first.findResponseBodies(it.second) }

    val errors = result
        .filterIsInstance<InvalidAbstractType>()

    if(errors.isNotEmpty())
        return KlutterResponseScanningNok(errors.map { it.data }.toList())

    val types = result
        .filterIsInstance<ValidAbstractType>()
        .map { it.data }
        .filterIsInstance<CustomType>()

    val duplicateTypes = types
        .groupBy { it.className }
        .filter { it.value.size > 1 }
        .map { it.key }

    val distinctTypes = mutableSetOf<CustomType>()
        .also { types.forEach { response ->
            it.addAll(response.distinctCustomTypes()) }
        }
        .filterDuplicates(types)

    val unknownTypes = distinctTypes
        .map { it.className }
        .filter { types.none { type -> type.className == it } }

    val emptyTypes = distinctTypes
        .filter { it.fields.isEmpty() }
        .map { it.className }

    return when {
        unknownTypes.isNotEmpty() ->
            unknownKlutterResponseError(unknownTypes)

        emptyTypes.isNotEmpty() ->
            emptyKlutterResponseError(emptyTypes)

        duplicateTypes.isNotEmpty() ->
            duplicateKlutterResponseError(duplicateTypes)

        else -> KlutterResponseScanningOk(distinctTypes.toList())
    }

}

private fun CustomType.distinctCustomTypes(
    output: MutableSet<CustomType> = mutableSetOf()
): Set<CustomType> {

    // CustomType with fields is present so break.
    if(output.contains(this)) return output

    // CustomType without fields is empty so replace with current.
    output.removeIf { it.className == this.className && it.fields.isEmpty()}
    output.add(this)

    // Add all fields of type CustomType.
    for(field in this.fields.map { it.type }.filterIsInstance<CustomType>()) {
        output.addAll(field.distinctCustomTypes(output))
    }

    return output
}

/**
 * Remove any CustomType without fields if there is a CustomType present with fields.
 *
 * If a CustomType has a TypeMember that itself is a CustomType,
 * then replace its field with a top level type.
 *
 * Example:
 * 1 CustomType Foo has TypeMember bar of Type Bar.
 * 2 CustomType Bar has TypeMember str of String, and count of Int.
 *
 * When processing Foo then Foo is CustomType and has fields so that CustomType is retained.
 * When processing the TypeMembers of Foo then Bar is a TypeMember of CustomType Bar but has no fields.
 * This function will find CustomType Bar in [types] list (top level declarations) which does
 * have the fields (and if it does not then it is an invalid declaration which will result in an [Either.nok]),
 * e.g. TypeMember str of StandardType String and TypeMember count of StandardType Int.
 * These fields will be added to TypeMember bar of CustomType Bar in CustomType Foo.
 */
private fun Set<CustomType>.filterDuplicates(
    /**
     * Top level types which are scanned.
     *
     * The CustomType Set contains top level types + CustomTypes which are found in TypeMembers.
     */
    types: List<CustomType>
): Set<CustomType> = groupBy { it.className }
    // Get a CustomType that contains fields or use the first entry
    .map { it.value.firstOrNull { value -> value.fields.isNotEmpty() } ?: it.value.first() }
    .map {
        it.copy(fields = it.fields.map { field ->
            field.type.let { type ->
                when {
                    type !is CustomType -> field

                    type.fields.isNotEmpty() -> field

                    types.none { t -> t.className == type.className } -> field

                    else -> TypeMember(field.name, types.first { t -> t.className == type.className })
                }
            }
        }
    )
}.toSet()

/**
 * Read the content of a Kotlin class File and return all classes as CustomType.
 */
private fun File.findResponseBodies(packageName: String) = this
    .readText()
    .replace("""\s+""".toRegex(), " ")
    .let { klutterJsonRegex.findAll(it) }
    .map { it.procesResponseBody(packageName) }
    .toList()

/**
 * Proces the [klutterJsonRegex] result and return a CustomType.
 *
 * This method only returns a CustomType result if:
 * - The class is open.
 * - The class extends KlutterJSON.
 * - The class is immutable.
 *
 * Example of valid declaration:
 *
 * ```
 * @Serializable
 * @KlutterResponse
 * open class Something(
 *      val x: String?,
 *      val y: SomethingElse
 * ): KlutterJSON<Something>() {
 *
 *  override fun data() = this
 *
 *  override fun strategy() = serializer()
 *
 * }
 * ```
 */
private fun MatchResult.procesResponseBody(packageName: String): Either<String, AbstractType> {
    val values = groupValues

    val className = values[3].trim().also {
        if(it != values.last()) {
            return processingError(
                msg = "KlutterJSON TypeParameter does not match class name: $it | ${values.last()}"
            )
        }
    }

    values[1].verifyAnnotationsPresent(className)
        ?.let { return it }

    val maybeFields = values[4]
        .split(",")
        .toMutableList()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.toTypeMember() }

    val fields = maybeFields
        .filterIsInstance<ValidTypeMember>()
        .map { it.data }

    val errors = maybeFields
        .filterIsInstance<InvalidTypeMember>()

    return if(errors.isEmpty()) {
        Either.ok(
            data = CustomType(
                className = className,
                packageName = packageName,
                fields = fields
            )
        )
    } else {
        processingError(msg = errors.joinToString { it.data })
    }
}

/**
 * Verify @KlutterResponse and @Serializable annotations are present.
 *
 * Returns [Either.nok] if an annotation is missing and otherwise null.
 */
private fun String.verifyAnnotationsPresent(
    className: String
): Either<String, AbstractType>? {

    val annotations = this
        .split(" ")
        .toMutableList()
        .map { it.trim() }
        .filter { it.isNotBlank() }

    if(!annotations.contains("@KlutterResponse"))
        return missingKlutterResponseAnnotation(name = className)

    if(!annotations.contains("@Serializable"))
        return missingSerializableAnnotation(name = className)

    return null
}