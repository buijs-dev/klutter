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

package dev.buijs.klutter.plugins.gradle.tasks.adapter.protobuf


import dev.buijs.klutter.core.*

private const val SPACING = "\r\n\r\n"

/**
 * @author Gillian Buijs
 */
class ProtoPrinter(private val objects: ProtoObjects): KlutterPrinter {

    override fun print(): String {

        val messages = objects.messages.joinToString(SPACING) {
            MessagePrinter(it).print()
        }

        val enumerations = objects.enumerations.joinToString(SPACING) {
            EnumerationPrinter(it).print()
        }

        return """
           |syntax = "proto3";
           |  
           |$messages
           |
           |$enumerations
           |
        """.trimMargin()
    }

}

/**
 * Prints all members of a class.
 *
 * @author Gillian Buijs
 */
internal class EnumerationPrinter(private val message: ProtoEnum): KlutterPrinter {

    override fun print() = """
        |enum ${message.name} {
        ${printValues(message.values)}
        |}
    """.trimMargin()

    private fun printValues(values: List<String>): String {
        var index = 0
        return values.joinToString("\r\n") { "|    $it = ${index++};" }
    }

}

/**
 * Prints all members of a class.
 *
 * @author Gillian Buijs
 */
internal class MessagePrinter(private val message: ProtoMessage): KlutterPrinter {

    override fun print() = """
        |message ${message.name} {
        ${MemberPrinter(message.fields).print()}
        |}
    """.trimMargin()

}

/**
 * Prints all members of a class.
 *
 * @author Gillian Buijs
 */
internal class MemberPrinter(private val fields: List<ProtoField>): KlutterPrinter {

    var index = 1

    override fun print() = fields
        .sortedBy { it.optional }
        .sortedBy { it.repeated }
        .sortedBy { it.name }
        .joinToString("\r\n") { printField(it) }

    private fun printField(field: ProtoField) : String {

        val dataType = field.customDataType?:field.dataType.type

        val sb = StringBuilder().also { sb ->
            sb.append("|    ")
            if(field.optional) sb.append("optional ")
            if(field.repeated) sb.append("repeated ")
            sb.append("$dataType ${field.name} = ${index++};")
        }

        return sb.toString()
    }

}
