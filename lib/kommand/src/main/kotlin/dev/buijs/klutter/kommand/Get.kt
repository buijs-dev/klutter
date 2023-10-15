package dev.buijs.klutter.kommand

fun List<String>.getDependency() {
    val mutable = toMutableList()
    val first = mutable.removeFirstOrNull()
    when {
        first == "flutter" ->
            mutable.downloadFlutterByCommand()
        else -> {
            println("I don't know what to get: $this")
        }
    }
}