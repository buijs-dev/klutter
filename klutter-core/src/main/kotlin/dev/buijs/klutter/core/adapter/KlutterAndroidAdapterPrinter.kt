package dev.buijs.klutter.core.adapter

/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
internal class KlutterAndroidAdapterPrinter {

    fun print(definitions: List<MethodCallDefinition>): String =
        printClass(definitions)

    private fun printFun(definition: MethodCallDefinition): String {
        return """
            | if (call.method == "${definition.getter}") {
            |            result.success(${definition.call})
            |        } """.trimMargin()
    }

    private fun printClass(definitions: List<MethodCallDefinition>): String {

        val block = if (definitions.isEmpty()) {
            "return result.notImplemented()"
        } else {
            val defs = definitions.joinToString("else") { printFun(it) }
            "$defs else result.notImplemented()"
        }

        val imports = definitions
            .map { it.import }
            .distinct()
            .joinToString("\r\n") { "import $it" }

        return """
            |package dev.buijs.klutter.adapter
            |
            |$imports
            |import io.flutter.plugin.common.MethodChannel
            |import io.flutter.plugin.common.MethodChannel.Result
            |import io.flutter.plugin.common.MethodCall
            |
            |/**
            | * Generated code By Gillian Buijs
            | *
            | * For bugs or improvements contact me: https://buijs.dev
            | *
            | */
            |class GeneratedKlutterAdapter {
            |
            |  fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
            |       $block
            |  }
            |
            |}
            """.trimMargin()
    }

}