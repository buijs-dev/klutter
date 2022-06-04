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

package dev.buijs.klutter.core

/**
 * @author Gillian Buijs
 *
 * This is not an exhaustive mapping but a
 * basic mapping to facilitate converting
 * Kotlin DTO classes to Dart DTO classes.
 *
 */
internal enum class DartKotlinMap(val kotlinType: String, val dartType: String) {

    INTEGER("Int", "int"),
    DOUBLE("Double", "double"),
    BOOLEAN("Boolean", "bool"),
    STRING("String", "String");

    companion object {

        fun toKotlinType(type: String) = values().firstOrNull { it.dartType == type } ?.kotlinType
            ?: throw KlutterCodeGenerationException("No such kotlinType in KotlinDartMap: $type")

        fun toDartType(type: String) = values().firstOrNull { it.kotlinType == type } ?.dartType
            ?: throw KlutterCodeGenerationException("No such dartType in KotlinDartMap: $type")

        fun toMap(type: String) = values().firstOrNull { it.dartType == type || it.kotlinType == type}
            ?: throw KlutterCodeGenerationException("No such dartType or kotlinType in KotlinDartMap: $type")

        fun toMapOrNull(type: String) = values().firstOrNull { it.dartType == type || it.kotlinType == type}

    }
}

/**
 * @author Gillian Buijs
 */
internal data class DartObjects(
    val messages: List<DartMessage>,
    val enumerations: List<DartEnum>
)

/**
 * @author Gillian Buijs
 */
internal data class DartMessage(
    val name: String,
    val fields: List<DartField>
)

/**
 * @property name enum class name.
 * @property values the enum constants.
 * @property jsonValues the serializable values.
 *
 * @author Gillian Buijs
 */
internal data class DartEnum(
    val name: String,
    val values: List<String>,
    val jsonValues: List<String>,
)

/**
 * @author Gillian Buijs
 */
internal data class DartField(
    val dataType: String,
    val name: String,
    val optional: Boolean,
    val isList: Boolean,
    var customDataType: String? = null,
)