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
 * Type of Proto being either a message or an enum.
 *
 * It's a ProtoType! :-)
 *
 * @author Gillian Buijs
 */
enum class ProtoObjectType(val type: String) {
    MESSAGE("message"),
    ENUM("enum")
}

/**
 * Mapping of Data Type between protobuf and Kotlin.
 *
 * @author Gillian Buijs
 */
enum class ProtoDataType(val type: String, val kotlinType: String) {
    DOUBLE("double", "Double"),
    FLOAT("float", "Float"),
    INTEGER("int32", "Int"),
    LONG("int64", "Long"),
    BOOLEAN("bool", "Boolean"),
    STRING("string", "String"),
    NONE("", "")
}

/**
 * @author Gillian Buijs
 */
data class ProtoObjects(
    val messages: List<ProtoMessage>,
    val enumerations: List<ProtoEnum>
)

/**
 * @author Gillian Buijs
 */
data class ProtoMessage(
    val name: String,
    val fields: List<ProtoField>
)

/**
 * @author Gillian Buijs
 */
data class ProtoEnum(
    val name: String,
    val values: List<String>
)

/**
 * @author Gillian Buijs
 */
data class ProtoField(
    val dataType: ProtoDataType,
    val name: String,
    val optional: Boolean,
    val repeated: Boolean,
    var customDataType: String? = null,
)

