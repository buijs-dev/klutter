package dev.buijs.klutter.kradle

import java.io.File

fun build(currentFolder: File) {
    println("Let's build something great!")
    listOf("clean build -p \"platform\"").execGradleCommand(currentFolder)
    println("Finished project build.")
}