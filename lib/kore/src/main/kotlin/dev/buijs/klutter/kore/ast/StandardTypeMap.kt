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

import dev.buijs.klutter.kore.KlutterException

/**
 * This is not an exhaustive mapping but a basic mapping to
 * facilitate converting Kotlin DTO classes to Dart DTO classes.
 */
internal enum class StandardTypeMap(
    val kotlinType: String,
    val dartType: String,
) {

    INTEGER("Int", "int"),
    DOUBLE("Double", "double"),
    BOOLEAN("Boolean", "bool"),
    STRING("String", "String"),
    NOTHING("Unit", "void"),
    LONG("Long", "int"),
    BYTE_ARRAY("ByteArray", "Uint8List"),
    INT_ARRAY("IntArray", "Int32List"),
    LONG_ARRAY("LongArray", "Int64List"),
    FLOAT_ARRAY("FloatArray", "Float32List"),
    DOUBLE_ARRAY("DoubleArray", "Float64List"),
    LIST("List", "List"),
    MAP("Map", "Map");

    companion object {

        @JvmStatic
        fun toKotlinType(type: String) = toMap(type).kotlinType

        @JvmStatic
        fun toDartType(type: String) = toMap(type).dartType

        @JvmStatic
        fun toMap(type: String) = toMapOrNull(type)
            ?: throw KlutterException("No such type in KotlinDartMap: $type")

        @JvmStatic
        fun toMapOrNull(type: String) = values()
            .firstOrNull { it.dartType == type || it.kotlinType == type}

        @JvmStatic
        fun fromKotlinTypeOrNull(type: String) = values()
            .firstOrNull { it.kotlinType == type}
    }
}