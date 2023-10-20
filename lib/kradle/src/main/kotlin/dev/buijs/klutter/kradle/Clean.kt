package dev.buijs.klutter.kradle

fun MutableList<String>.clean() {
    val first = removeFirstOrNull()
    when {
        first == "cache" ->
            downloadFlutterByCommand()
        else -> {
            println("I don't know what to clean: $this")
        }
    }
}