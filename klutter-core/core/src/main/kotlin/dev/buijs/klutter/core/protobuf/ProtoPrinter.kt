package dev.buijs.klutter.core.protobuf

import dev.buijs.klutter.core.KlutterPrinter

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
