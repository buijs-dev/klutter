package dev.buijs.klutter.kore.ast

import kotlinx.serialization.Serializable
import java.io.File

/**
 * Parent for all types that are serializable to a format that dart squint can process.
 */
sealed interface SquintType

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
    val className: String,
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
    val className: String,
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