package dev.buijs.klutter.kradle

import dev.buijs.klutter.kore.tasks.CleanCacheTask

fun MutableList<String>.clean() {
    val first = removeFirstOrNull()
    when {
        first == "cache" -> {
            println("The .kradle/cache folder will be cleaned...")
            CleanCacheTask().run()
            println("Finished .kradle/cache cleaning.")
        }
        else -> {
            println("I don't know what to clean: $this")
        }
    }
}