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

package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.KlutterException
import mu.KotlinLogging

private val regex = """val ([^:]+?): (.+)""".toRegex()

private val log = KotlinLogging.logger { }

/**
 * Data type defined in Dart language.
 *
 * @property type the actual type of data.
 * @property name the name of the field.
 * @property isOptional [Boolean] value indicating a field is nullable.
 * @property isList [Boolean] value indicating a field is nested in a [List].
 * @property isCustomType [Boolean] value indicating a data type
 * is custom or a standard Dart type as defined in [DartKotlinMap].
 *
 */
data class DartField(
    val type: String,
    val name: String,
    val isList: Boolean,
    val isOptional: Boolean,
    val isCustomType: Boolean,
)

/**
 * Process a single line and try to create a DartField object.
 *
 * @return [DartField]
 */
fun String.toDartField(): DartField? {

    val match = regex.find(this)

    return when {

        this.isBlank() -> null

        match == null -> {
            log.debug {"Invalid field declaration: $this" }; null
        }

        else -> {
            match.groupValues.determineName().determineDataType().toDartField()
        }

    }

}

/**
 * Determine the name of the field and if it is optional or not.
 */
private fun List<String>.determineName(): Data {
    return this[1].trim().let {
        when {
            it.isBlank() -> {
                throw KlutterException("Could not determine name of field.")
            }

            it.contains(" ") -> {
                throw KlutterException("Name of field is invalid: '$it'")
            }

            else -> Data(name = it, type = this[2])
        }
    }
}

/**
 * Determine the data type declaration of the field.
 */
private fun Data.determineDataType(): Data {
    val t: String = type
        .filter { !it.isWhitespace() }
        .removeSuffix(",")

        // Setting a field to null explicitly in Kotlin
        // gives the ability to have optional fields in JSON.
        //
        // Without the '= null' deserialization would
        // fail if the key is missing.
        .removeSuffix("=null")

    // If at this point the type still contains a default
    // value then processing can not continue.
    //
    // The DTO to be generated in Dart can not have default
    // values for an immutable field.
    if(t.contains("=")){
        throw KlutterException("A KlutterResponse DTO can not have default values.")
    }

    return Data(name = name, type = t)

}

/**
 * Convert the String content to an instance of [DartField].
 */
private fun Data.toDartField(): DartField {

    // Maybe get the data type nested within 'List<...>'
    val t = type.unwrapFromList().removeSuffix("?")

    // If unwrapping returned dataType then it is not a List
    val isList = t != type.removeSuffix("?")

    val optional = type.endsWith("?")

    val dataType = DartKotlinMap.toMapOrNull(t)?.dartType

    return DartField(
        name = name,
        isList = isList,
        isOptional = optional,
        type = dataType ?: t,
        isCustomType = dataType == null
    )

}

/**
 * Helper class to store parsed line data.
 */
private data class Data(
    /**
     * The field name.
     */
    val name: String,

    /**
     * The field type.
     */
    val type: String,
)