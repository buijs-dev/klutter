package dev.buijs.klutter.core

annotation class KlutterAdapter(val method: String)

data class MethodCallDefinition(
    val methodName: String,
    val methodCall: String,
    val methodReturnValue: String
)

class MethodCallPrinter {
    fun print(definition: MethodCallDefinition): String {
        return "if (call.method == \"${definition.methodName}\") {\n" +
                "result.success(\"${definition.methodCall}\")\n}"
    }
}