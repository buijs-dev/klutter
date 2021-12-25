package dev.buijs.klutter.core.adapter.service

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
class PigeonDartClassPrinter {

    fun print(dto: KlutterServiceDTO): String {
        val sb = StringBuilder()
        sb.append("import 'package:pigeon/pigeon.dart';\r\n\r\n")
        dto.apis.forEach { api -> sb.append(print(api)) }
        dto.dataClasses.forEach { dc -> sb.append(print(dc)) }
        dto.enumClasses.forEach { ec -> sb.append(print(ec)) }
        return sb.toString()
    }

    private fun print(enumClass: KlutterEnumClass): String {
        val values = enumClass.values?.joinToString("\r\n") { "  $it," }
        return "enum ${enumClass.name} {\r\n$values\r\n}\r\n\r\n"
    }

    private fun print(dataClass: KlutterDataClass): String {
        val name = dataClass.name
        val fields = dataClass.fields?.joinToString("\r\n") { "  ${it.type}? ${it.name};" }

        return """
            |class $name {
            |$fields
            |}
            |
            |
        """.trimMargin()
    }

    private fun print(api: KlutterApi): String {
        val functions = api.functions?.joinToString("\r\n\r\n") { print(it) }

        return """|@HostApi()
                  |abstract class ${api.name} {
                  |$functions
                  |}
                  |
                  |
                """.trimMargin()
    }

    private fun print(apiFunction: KlutterApiFunction): String {
        val name = apiFunction.name
        val returnValue = apiFunction.returnValue?:"void"
        val arguments = apiFunction.parameters?.joinToString(", ") { "${it.type} ${it.name}" } ?: ""

        return if(apiFunction.async)
        """ |  @async
            |  $returnValue $name($arguments);
        """.trimMargin() else
            """
            |  $returnValue $name($arguments);
        """.trimMargin()

    }

}