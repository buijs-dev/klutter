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

import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.ast.Controller
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk

/**
 * Alias for [Either] with only right value of type List of CustomType.
 */
internal typealias Valid =
        EitherOk<List<String>, List<Controller>>

/**
 * Alias for [Either] with only left value of type List of [String] (error).
 */
internal typealias Invalid =
        EitherNok<List<String>, List<Controller>>

/**
 * Response for [validateResponses].
 */
internal typealias ValidationResult =
        Either<List<String>, List<Controller>>

/**
 * Response for [validateResponses].
 */
internal typealias ValidationResultSquintMessages =
        Either<List<String>, List<SquintMessageSource>>

/**
 * Alias for [Either] with only right value of type List of CustomType.
 */
internal typealias ValidSquintMessages =
        EitherOk<List<String>, List<SquintMessageSource>>

/**
 * Alias for [Either] with only left value of type List of [String] (error).
 */
internal typealias InvalidSquintMessages =
        EitherNok<List<String>, List<SquintMessageSource>>

/**
 * Error indicating multiple KlutterResponse classes have the same class name.
 */
private fun duplicateKlutterResponseError(types: List<String>) = Invalid(
    listOf("KlutterResponse contract violation! Duplicate class names: $types")
)

/**
 * Error indicating multiple KlutterResponse classes have the same class name.
 */
private fun duplicateKlutterResponseErrorSquint(types: List<String>) = InvalidSquintMessages(
    listOf("KlutterResponse contract violation! Duplicate class names: $types")
)

/**
 * Error indicating a KlutterResponse class has a TypeMember of Unknown CustomType.
 */
private fun unknownKlutterResponseError(types: List<String>) = Invalid(
    listOf("Unknown KlutterResponse TypeMember: $types")
)

/**
 * Error indicating a KlutterResponse class has a TypeMember of Unknown CustomType.
 */
private fun unknownKlutterResponseErrorSquint(types: List<String>) = InvalidSquintMessages(
    listOf("Unknown KlutterResponse TypeMember: $types")
)

/**
 * Error indicating the source File is missing.
 *
 * Without a source File, the dart squint library can not generate dart code.
 */
private fun missingSourceFileErrorSquint(types: List<String>) = InvalidSquintMessages(
    listOf("Source File from which to generate dart code is missing: $types")
)

/**
 * Error indicating a KlutterResponse class has no members.
 */
private fun emptyKlutterResponseError(types: List<String>) = InvalidSquintMessages(
    listOf("KlutterResponse contract violation! Some classes have no fields: $types")
)

/**
 * Validate list of KlutterResponse classes.
 * - List of KlutterResponse [AbstractType] if all are valid
 * - List of [String] errors if not.
 */
internal fun List<Either<String,SquintMessageSource>>.validateResponses(): ValidationResultSquintMessages {

    val errors = this
        .filterIsInstance<InvalidSquintType>()

    if(errors.isNotEmpty())
        return InvalidSquintMessages(errors.map { it.data }.toList())

    val metadata = this
        .filterIsInstance<ValidSquintType>()
        .map { it.data }

    val noSourceFile = metadata.filter { it.source == null }

    val duplicateTypes = metadata.duplicateSource()

    val distinctTypes = metadata.distinctSource()

    val unknownTypes = distinctTypes.unknown(metadata.map { it.type }.filterIsInstance<CustomType>())

    val emptyTypes = distinctTypes
        .filter { it.members.isEmpty() }
        .map { it.className }

    return when {
        noSourceFile.isNotEmpty() ->
            missingSourceFileErrorSquint(unknownTypes)

        unknownTypes.isNotEmpty() ->
            unknownKlutterResponseErrorSquint(unknownTypes)

        emptyTypes.isNotEmpty() ->
            emptyKlutterResponseError(emptyTypes)

        duplicateTypes.isNotEmpty() ->
            duplicateKlutterResponseErrorSquint(duplicateTypes)

        else -> ValidSquintMessages(metadata)
    }

}

/**
 * Find all valid Controller classes.
 * - List of KlutterResponse [AbstractType] if all are valid
 * - List of [String] errors if not.
 */
internal fun List<Either<String,Controller>>.validateControllers(): ValidationResult {

    val errors = this
        .filterIsInstance<InvalidControllerType>()

    if(errors.isNotEmpty())
        return Invalid(errors.map { it.data }.toList())

    val types = this
        .filterIsInstance<ValidControllerType>()
        .map { it.data }

    // TODO fix validation.
//    val duplicateTypes = types.duplicates()
//
//    val distinctTypes = types.distinct()
//
//    val unknownTypes = distinctTypes.unknown(types)
//
//    return when {
//        unknownTypes.isNotEmpty() ->
//            unknownKlutterResponseError(unknownTypes)
//
//        duplicateTypes.isNotEmpty() ->
//            duplicateKlutterResponseError(duplicateTypes)
//
//        else -> Valid(distinctTypes.filterIsInstance<ControllerType>().toList())
//    }

    return Valid(types)
}

private fun List<SquintMessageSource>.duplicateSource() = this
    .groupBy { it.type.className }
    .filter { it.value.size > 1 }
    .map { it.key }

private fun List<CustomType>.duplicates() = this
    .groupBy { it.className }
    .filter { it.value.size > 1 }
    .map { it.key }

private fun List<Controller>.distinct() =
    mutableSetOf<Controller>().also { set ->
        this.forEach { type ->
            set.addAll(type.distinctCustomTypes().filterIsInstance<Controller>())
        }
    }.filterDuplicates(this)

private fun List<SquintMessageSource>.distinctSource() =
    mutableSetOf<CustomType>().also { set ->
        this.forEach { type ->
            set.addAll(type.distinctCustomTypes())
        }
    }.filterDuplicates(this.map { it.type }.filterIsInstance<CustomType>())

private fun Set<CustomType>.unknown(types: List<CustomType>) = this
    .map { it.className }
    .filter { types.none { type -> type.className == it } }

private fun SquintMessageSource.distinctCustomTypes(
    output: MutableSet<CustomType> = mutableSetOf()
): Set<CustomType> = if(type is CustomType) {
    (type as CustomType).distinctCustomTypes(output)
} else {
    output
}

private fun CustomType.distinctCustomTypes(
    output: MutableSet<CustomType> = mutableSetOf()
): Set<CustomType> {

    // CustomType with fields is present so break.
    if(output.contains(this)) return output

    // CustomType without fields is empty so replace with current.
    output.removeIf { it.className == this.className && it.members.isEmpty()}
    output.add(this)

    // Add all fields of type CustomType.
    for(field in this.members.map { it.type }.filterIsInstance<CustomType>()) {
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
    .map { it.value.firstOrNull { value -> value.members.isNotEmpty() } ?: it.value.first() }
    .map {
        it.copy(fields = it.members.map { field ->
            field.type.let { type ->
                when {
                    type !is CustomType -> field

                    type.members.isNotEmpty() -> field

                    types.none { t -> t.className == type.className } -> field

                    else -> TypeMember(field.name, types.first { t -> t.className == type.className })
                }
            }
        }
    )
}.toSet()