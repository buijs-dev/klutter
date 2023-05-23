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

import dev.buijs.klutter.kore.common.removeSuffixIfPresent
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import dev.buijs.klutter.kore.common.Either

/**
 * Alias for [Either] with only right value of type [TypeMember].
 */
typealias ValidTypeMember =
        EitherOk<String, TypeMember>

/**
 * Alias for [Either] with only left value of type [String] (error).
 */
typealias InvalidTypeMember =
        EitherNok<String, TypeMember>

/**
 * Alias for [Either] with only right value of type [AbstractType].
 */
typealias ValidAbstractType =
        EitherOk<String, AbstractType>

/**
 * Alias for [Either] with only left value of type [String] (error).
 */
typealias InvalidAbstractType =
        EitherNok<String, AbstractType>

/**
 * Raw String wrapper which contains a Type.
 */
data class TypeData(

    /**
     * The actual type.
     */
    val type: String,

    /**
     * Nullable indicator.
     */
    val nullable: Boolean,

    ) {

    constructor(raw: String) : this(
        type = raw.trim().removeSuffixIfPresent("?"),
        nullable = raw.trim().endsWith("?")
    )

    constructor(type: String, arguments: List<String>, nullable: Boolean) : this(
        type = if(arguments.isEmpty()) type else "${type}<${arguments.joinToString()}>",
        nullable = nullable
    )
}

/**
 * Return a [StandardType] or [CustomType] wrapped in [ValidAbstractType] or [InvalidAbstractType]:
 * - [StandardType] if member of a kotlin type [StandardTypeMap]
 * - [CustomType] if not.
 *
 * A StandardType can be valid or invalid.
 * A CustomType is always wrapped in [ValidAbstractType].
 */
fun TypeData.toStandardTypeOrUndetermined() =
    findStandardType() ?: toUndetermined()

/**
 * Regex to find a Map type, and it's key-value type.
 */
private val mapTypeRegex =
    """^(Map)(<([^,]*?),([^>]*?)>|)""".toRegex()

/**
 * Regex to find a List type, and it's value type.
 */
private val listTypeRegex =
    """^(List)(<([^>]*?)>|)""".toRegex()

/**
 * Regex to verify a class name is valid.
 */
private val customClassNameRegex =
    """^[A-Z][a-zA-Z0-9]+$""".toRegex()

/**
 * Error indicating the [mapTypeRegex] match is incomplete.
 */
private fun List<String>.mapTypeRegexFailure() =
    InvalidAbstractType("MapType could not be processed: '$this'")

/**
 * Error indicating the [AbstractType] key of Map could not be determined.
 */
private fun String.invalidMapKey() =
    InvalidAbstractType("KeyType of Map could not be processed: '$this'")

/**
 * Error indicating the [AbstractType] value of Map could not be determined.
 */
private fun String.invalidMapValue() =
    InvalidAbstractType("ValueType of Map could not be processed: '$this'")

/**
 * Error indicating the [mapTypeRegex] match is incomplete.
 */
private fun List<String>.listTypeRegexFailure() =
    InvalidAbstractType("ListType could not be processed: '$this'")

/**
 * Error indicating the [AbstractType] value of List could not be determined.
 */
private fun String.invalidListValue() =
    InvalidAbstractType("ValueType of List could not be processed: '$this'")

/**
 * Return [ValidAbstractType] with type:
 * - [NullableCustomType] if nullable
 * - [CustomType] if not.
 */
private fun TypeData.toUndetermined() = when {
    customClassNameRegex.find(type) == null ->
        InvalidAbstractType("Type name is invalid (should match $customClassNameRegex): $type")

    nullable ->
        ValidAbstractType(UndeterminedType(type))

    else ->
        ValidAbstractType(UndeterminedType(type))
}

/**
 * Find a Type which matches a [StandardType] or return null.
 */
private fun TypeData.findStandardType(): Either<String, AbstractType>? {

    val mapMatch =
        mapTypeRegex.find(type)

    if (mapMatch != null)
        return mapMatch.groupValues.toMapType(nullable)

    val listMatch =
        listTypeRegex.find(type)

    if (listMatch != null)
        return listMatch.groupValues.toListType(nullable)

    // At this point it is NOT a repeated type
    // so try to parse as-is to StandardType.
    //
    // By definition this will not return a
    // Map or List so if a match is found it can
    // be wrapped in a StandardDataType.
    return standardTypeLookup(type, nullable)
        ?.let { ValidAbstractType(data = it) }

}

/**
 * Process the MatchResult of regex [mapTypeRegex] and return
 * [ValidAbstractType] if Map-Key + Map-Value are determined or
 * [InvalidAbstractType] if processing failed.
 */
private fun List<String>.toMapType(
    nullable: Boolean
): Either<String, AbstractType> {

    if (this.size < 5)
        return mapTypeRegexFailure()

    if(this[2].trim() == "")
        return ValidAbstractType(if(nullable) NullableMapType() else MapType())

    val key = TypeData(this[3])
        .toStandardTypeOrUndetermined()
        .validAbstractTypeOrNull()

    val value = TypeData(this[4])
        .toStandardTypeOrUndetermined()
        .validAbstractTypeOrNull()

    return when {
        key == null ->
            this[3].invalidMapKey()
        value == null ->
            this[4].invalidMapValue()
        nullable ->
            ValidAbstractType(data = NullableMapType(key, value))
        else ->
            ValidAbstractType(data = MapType(key, value))
    }

}

/**
 * Process the MatchResult of regex [listTypeRegex] and return
 * [ValidAbstractType] if List-Value is determined or
 * [InvalidAbstractType] if processing failed.
 */
private fun List<String>.toListType(
    nullable: Boolean
): Either<String, AbstractType> {

    if (this.size < 4)
        return listTypeRegexFailure()

    if(this[3].trim() == "")
        return ValidAbstractType(if(nullable) NullableListType() else ListType())

    val childType = TypeData(this[3])
        .toStandardTypeOrUndetermined()
        .validAbstractTypeOrNull()

    return when {
        childType == null ->
            this[3].invalidListValue()
        nullable ->
            ValidAbstractType(data = NullableListType(childType))
        else ->
            ValidAbstractType(data = ListType(childType))
    }

}

/**
 * Return [AbstractType] data if type is [ValidAbstractType] or null if not.
 */
private fun Either<String, AbstractType>.validAbstractTypeOrNull() =
    if (this !is ValidAbstractType) null else this.data

/**
 * Convert a String value to a [StandardTypeMap]
 * and lookup the corresponding [StandardType]
 * in [standardTypes] or [nullableStandardTypes].
 *
 * Returns null if:
 * - [type] is not a Kotlin type present in StandardTypeMap.
 * - [type] is a Map.
 * - [type] is a List.
 */
private fun standardTypeLookup(
    type: String,
    nullable: Boolean
): AbstractType? {
    val typeOrNull = StandardTypeMap.fromKotlinTypeOrNull(type)

    return when {
        typeOrNull == null -> null
        nullable -> nullableStandardTypes[typeOrNull]
        else -> standardTypes[typeOrNull]
    }

}

/**
 * Basic mapping for not nullable types.
 *
 * This map contains all [StandardTypeMap] types except for:
 * - Map
 * - List
 *
 * Map and List cannot be processed directly.
 * The key and value type need to be extracted first.
 * See [toMapType] and [toListType].
 */
private val standardTypes = mapOf(
    StandardTypeMap.INTEGER to IntType(),
    StandardTypeMap.DOUBLE to DoubleType(),
    StandardTypeMap.BOOLEAN to BooleanType(),
    StandardTypeMap.STRING to StringType(),
    StandardTypeMap.LONG to LongType(),
    StandardTypeMap.BYTE_ARRAY to ByteArrayType(),
    StandardTypeMap.INT_ARRAY to IntArrayType(),
    StandardTypeMap.LONG_ARRAY to LongArrayType(),
    StandardTypeMap.FLOAT_ARRAY to FloatArrayType(),
    StandardTypeMap.DOUBLE_ARRAY to DoubleArrayType(),
    StandardTypeMap.NOTHING to UnitType(),
)

/**
 * Basic mapping for not nullable types.
 *
 * This map contains all [StandardTypeMap] types except for:
 * - Map
 * - List
 * - Unit
 *
 * Unit can not be null.
 * Map and List cannot be processed directly.
 * The key and value type need to be extracted first.
 * See [toMapType] and [toListType].
 */
private val nullableStandardTypes = mapOf<StandardTypeMap, StandardType>(
    StandardTypeMap.INTEGER to NullableIntType(),
    StandardTypeMap.DOUBLE to NullableDoubleType(),
    StandardTypeMap.BOOLEAN to NullableBooleanType(),
    StandardTypeMap.STRING to NullableStringType(),
    StandardTypeMap.LONG to NullableLongType(),
    StandardTypeMap.BYTE_ARRAY to NullableByteArrayType(),
    StandardTypeMap.INT_ARRAY to NullableIntArrayType(),
    StandardTypeMap.LONG_ARRAY to NullableLongArrayType(),
    StandardTypeMap.FLOAT_ARRAY to NullableFloatArrayType(),
    StandardTypeMap.DOUBLE_ARRAY to NullableDoubleArrayType(),
)