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
package dev.buijs.klutter.kore.ast

import kotlinx.serialization.Serializable
import java.io.File

/**
 * Parent for all types that are serializable to a format that dart squint can process.
 */
@Serializable
sealed interface SquintType {
    val className: String
}

/**
 * Data class to serialize a Klutter CustomType to JSON,
 * formatted in a way which can be read by
 * dart library Squint (https://pub.dev/packages/squint_json).
 *
 * Example:
 *
 * ```
 *  {
 *   "className": "MyResponse",
 *   "members": [
 *     {
 *         "name": "a1",
 *         "type": "int",
 *         "nullable": false
 *     },
 *     {
 *           "name": "a2",
 *           "type": "String",
 *           "nullable": true
 *     }
 *   ]
 * }
 * ```
 */
@Serializable
class SquintCustomType(
    override val className: String,
    val members: List<SquintCustomTypeMember>,
): SquintType

/**
 * A member of a CustomType [SquintCustomType].
 *
 * Example:
 * ```
 * {
 *    "name": "a1",
 *    "type": "int",
 *    "nullable": false
 *  }
 * ```
 */
@Serializable
class SquintCustomTypeMember(
    val name: String,
    val type: String,
    val nullable: Boolean,
)

@Serializable
class SquintEnumType(
    override val className: String,
    val values: List<String> = emptyList(),
    val valuesJSON: List<String> = emptyList(),
): SquintType

data class SquintMessageSource(
    /**
     * Scanned output as [AbstractType].
     */
    val type: AbstractType,

    /**
     * Type data to be consumed by dart squint.
     */
    val squintType: SquintType,

    /**
     * File link to squint type source.
     */
    val source: File? = null
)