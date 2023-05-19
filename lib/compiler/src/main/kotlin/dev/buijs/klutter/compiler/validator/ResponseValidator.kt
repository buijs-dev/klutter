/* Copyright (c) 2021 - 2023 Buijs Software
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
package dev.buijs.klutter.compiler.validator

import dev.buijs.klutter.compiler.processor.kcLogger
import dev.buijs.klutter.compiler.scanner.InvalidSquintType
import dev.buijs.klutter.compiler.scanner.ValidSquintType
import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.Either

/**
 * Find all valid Response classes.
 * </br>
 * Will return either:
 * <ul>
 *     <li>List of Response [CustomType] if all are valid.</li>
 *     <li>List of [String] errors if not.</li>
 * </ul>
 */
internal fun List<Either<String,SquintMessageSource>>.validateResponses(): ValidationResultSquintMessages {

    val errors = filterIsInstance<InvalidSquintType>()

    if(errors.isNotEmpty())
        return InvalidSquintMessages(errors.map { it.data }.toList())

    val metadata = filterIsInstance<ValidSquintType>()
        .map { it.data }
        .also { kcLogger?.info("Executing [validateResponses] with metadata: $it") }

    val noSourceFile = metadata
        .filter { it.source == null }
        .map { it.squintType.className }
        .also { kcLogger?.warn("Encountered SquintMessageSource without source files during [validateResponses]: $it") }

    val duplicateTypes = metadata
        .duplicateSource()
        .also { kcLogger?.warn("Encountered duplicate SquintMessageSource during [validateResponses]: $it") }

    val distinctCustomTypes = metadata
        .distinctCustomTypes()
        .also { kcLogger?.info("All distinct CustomTypes found during [validateResponses]: $it") }

    val unknownTypes = distinctCustomTypes
        .findUnknownTypes(metadata.map { it.type })
        .also { kcLogger?.warn("Encountered unknown Types during [validateResponses]: $it") }

    val emptyTypes = distinctCustomTypes
        .filter { it.members.isEmpty() }
        .also { kcLogger?.warn("Encountered CustomTypes without members during [validateResponses]: $it") }
        .map { "${it.packageName}.${it.className}" }

    return when {
        noSourceFile.isNotEmpty() ->
            missingSourceFileErrorSquint(noSourceFile)

        unknownTypes.isNotEmpty() ->
            unknownResponseErrorSquint(unknownTypes)

        emptyTypes.isNotEmpty() ->
            emptyResponseError(emptyTypes)

        duplicateTypes.isNotEmpty() ->
            duplicateResponseErrorSquint(duplicateTypes)

        else -> ValidSquintMessages(metadata)
    }

}

private fun List<SquintMessageSource>.duplicateSource() = this
    .groupBy { it.type.className }
    .filter { it.value.size > 1 }
    .map { it.key }

private fun List<SquintMessageSource>.distinctCustomTypes(): Set<CustomType> {

    val set = mutableSetOf<CustomType>()
    val customTypes = map { it.type }.filterIsInstance<CustomType>()
    customTypes.forEach { set.addOrReplaceIfApplicable(it) }
    kcLogger?.info("Executing [distinctCustomTypes] and found including possible duplicates: $set")
    val filtered = set.filterDuplicates(customTypes)
    kcLogger?.info("Executing [distinctCustomTypes] and removed all duplicates: $set")
    return filtered
}

private fun Set<CustomType>.findUnknownTypes(types: List<AbstractType>) = this
    .map { it.className }
    .filter { types.none { type -> type.className == it } }

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
    .map { it.copyOrReplace(types) }
    .toSet()

private fun CustomType.copyOrReplace(
    /**
     * Top level types which are scanned.
     *
     * The CustomType Set contains top level types + CustomTypes which are found in TypeMembers.
     */
    types: List<CustomType>
): CustomType = this.copy(
    fields = members.map {
        it.type.let { type ->
            when {
                type !is CustomType -> it

                type.members.isNotEmpty() -> it

                types.none { t -> t.className == type.className } -> it

                else -> TypeMember(it.name, types.first { t -> t.className == type.className })
            }
        }
    }
)