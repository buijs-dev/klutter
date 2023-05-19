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

/**
 * A not null Integer [StandardType].
 */
open class IntType: StandardType(type = StandardTypeMap.INTEGER)

/**
 * A nullable Integer [StandardType].
 */
open class NullableIntType: IntType(), Nullable

/**
 * A not null Double [StandardType].
 */
open class DoubleType: StandardType(type = StandardTypeMap.DOUBLE)

/**
 * A nullable Double [StandardType].
 */
open class NullableDoubleType: DoubleType(), Nullable

/**
 * A not null Boolean [StandardType].
 */
open class BooleanType: StandardType(type = StandardTypeMap.BOOLEAN)

/**
 * A nullable Boolean [StandardType].
 */
open class NullableBooleanType: BooleanType(), Nullable

/**
 * A not null String [StandardType].
 */
open class StringType: StandardType(type = StandardTypeMap.STRING)

/**
 * A nullable String [StandardType].
 */
open class NullableStringType: StringType(), Nullable

/**
 * Unit [StandardType].
 */
open class UnitType: StandardType(type = StandardTypeMap.NOTHING)

/**
 * A not null Long [StandardType].
 */
open class LongType: StandardType(type = StandardTypeMap.LONG)

/**
 * A nullable Long [StandardType].
 */
open class NullableLongType: LongType(), Nullable

/**
 * A not null ByteArray [StandardType].
 */
open class ByteArrayType: StandardType(type = StandardTypeMap.BYTE_ARRAY)

/**
 * A nullable ByteArray [StandardType].
 */
open class NullableByteArrayType: ByteArrayType(), Nullable

/**
 * A not null IntArray [StandardType].
 */
open class IntArrayType: StandardType(type = StandardTypeMap.INT_ARRAY)

/**
 * A nullable IntArray [StandardType].
 */
open class NullableIntArrayType: IntArrayType(), Nullable

/**
 * A not null LongArray [StandardType].
 */
open class LongArrayType: StandardType(type = StandardTypeMap.LONG_ARRAY)

/**
 * A nullable LongArray [StandardType].
 */
open class NullableLongArrayType: LongArrayType(), Nullable

/**
 * A not null FloatArray [StandardType].
 */
open class FloatArrayType: StandardType(type = StandardTypeMap.FLOAT_ARRAY)

/**
 * A nullable FloatArray [StandardType].
 */
open class NullableFloatArrayType: FloatArrayType(), Nullable

/**
 * A not null DoubleArray [StandardType].
 */
open class DoubleArrayType: StandardType(type = StandardTypeMap.DOUBLE_ARRAY)

/**
 * A nullable LongArray [StandardType].
 */
open class NullableDoubleArrayType: DoubleArrayType(), Nullable

/**
 * A not null List [StandardType].
 */
open class ListType(
    val child: AbstractType? = null,
): StandardType(type = StandardTypeMap.LIST), Nested

/**
 * A nullable List [StandardType].
 */
open class NullableListType(
    child: AbstractType? = null
): ListType(child), Nullable

/**
 * A not null Map [StandardType].
 */
open class MapType(
    val key: AbstractType? = null,
    val value: AbstractType? = null,
): StandardType(type = StandardTypeMap.MAP), Nested

/**
 * A nullable LongArray [StandardType].
 */
open class NullableMapType(
    key: AbstractType? = null,
    value: AbstractType? = null,
): MapType(key, value), Nullable